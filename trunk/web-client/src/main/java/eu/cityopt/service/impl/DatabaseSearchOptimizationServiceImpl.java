package eu.cityopt.service.impl;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.model.OptSetScenarios;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.OptSetScenariosRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.service.DatabaseSearchOptimizationService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.SearchOptimizationResults;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.service.OptimisationSupport;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;

@Service
public class DatabaseSearchOptimizationServiceImpl implements DatabaseSearchOptimizationService {
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	OptimizationSetRepository optimizationSetRepository;
	
	@Autowired
	OptSetScenariosRepository optSetScenariosRepository;
	
	@Autowired
	ScenarioRepository scenarioRepository;
	
	@Autowired
	OptimisationSupport optSupport;
	
	@Autowired 
	ModelMapper modelMapper;
	
	@PersistenceContext
	EntityManager em;
	
	@Transactional(readOnly = true)
	public SearchOptimizationResults searchConstEval(int prjId, int optId, int size) throws ParseException, ScriptException, EntityNotFoundException{   	
		Project project = projectRepository.findOne(prjId);
		
		OptimizationSet optimizationSet = optimizationSetRepository.findOne(optId);
		
		if(project == null)
			throw new EntityNotFoundException("could not find prjId: "+ prjId);
		if(optimizationSet == null)
			throw new EntityNotFoundException("could not find optId: "+ optId);
		
		if(optimizationSet.getProject().getPrjid() != prjId)
			throw new InvalidParameterException("optimization set is not part of the project" +optimizationSet);
		
		EvaluationResults er = optSupport.evaluateScenarios(project, optimizationSet); 
			
		return persistResultsFromSearchConstEval(prjId, optId, size, er);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public SearchOptimizationResults persistResultsFromSearchConstEval(int prjId, int optId, int size, EvaluationResults er ) throws ParseException, ScriptException, EntityNotFoundException{   	
		SearchOptimizationResults sor = new SearchOptimizationResults();
		sor.setEvaluationResult(er);
		sor.resultScenarios = new ArrayList<ScenarioWithObjFuncValueDTO>();
		
		if(!er.feasible.isEmpty()){
			
			//sort evaluation results by value
			Map<Integer, ObjectiveStatus> sortedMap = sortByValue(er.feasible);
//			sortedMap.forEach((Integer i, ObjectiveStatus s) -> System.out.println(s.objectiveValues[0]));
			OptimizationSet optimizationSet = optimizationSetRepository.findOne(optId);
			for(Integer scenId : sortedMap.keySet()){
				OptSetScenarios oss = new OptSetScenarios();
				oss.setOptimizationset(optimizationSet);
				Scenario scen = em.getReference(Scenario.class, scenId);
				oss.setScenario(scen);
				
				ObjectiveStatus other = er.feasible.get(scenId);
				Double value = other.objectiveValues[0];				
				
				oss.setValue(String.format(Locale.ENGLISH, "%s", value));
				optSetScenariosRepository.save(oss);
				
				//add to result list, as long as desired size is not reached 
				if(sor.resultScenarios.size() < size){	
					ScenarioWithObjFuncValueDTO scenWV= modelMapper.map(scen, ScenarioWithObjFuncValueDTO.class);
					scenWV.setValue(value);
					sor.resultScenarios.add(scenWV);
				}				
			}	
		}	
		return sor;
	}

	@Transactional
	@Override
	public void saveSearchOptimizationResult(int optId, int scenId) 
			throws EntityNotFoundException{
		OptimizationSet optimizationSet = optimizationSetRepository.findOne(optId);
		Scenario scen = null;
		
		if(optimizationSet == null)
			throw new EntityNotFoundException("could not find optId: "+ optId);
		
		if(scenId > 0){
			scen = scenarioRepository.findOne(scenId);
			if(scen == null)
				throw new EntityNotFoundException("could not find scenario with scenId: "+ scenId);
		}
		
		optimizationSet.setScenario(scen);
		optimizationSetRepository.save(optimizationSet);
	}
	
	@Transactional(readOnly = true)
	@Override
	public ScenarioDTO getSearchOptimizationResult(int optId) 
			throws EntityNotFoundException{
		OptimizationSet optimizationSet = optimizationSetRepository.findOne(optId);
		
		if(optimizationSet == null)
			throw new EntityNotFoundException("could not find optId: "+ optId);
		
		Scenario scen = optimizationSet.getScenario();
		
		return modelMapper.map(scen, ScenarioDTO.class);
	}

	/**
	 * sorts objectiveStatus map by value, starting with the optimal scenario
	 * @param map
	 * @return
	 */
	private <K,T> Map<K, ObjectiveStatus> sortByValue( Map<K, ObjectiveStatus> map )
	{
	     Map<K,ObjectiveStatus> result = new LinkedHashMap<>();
	     Stream <Entry<K,ObjectiveStatus>> st = map.entrySet().stream();
	     
	     st.sorted(new Comparator<Entry<K,ObjectiveStatus>>(){
				@Override
				public int compare(Entry<K, ObjectiveStatus> arg0,
						Entry<K, ObjectiveStatus> arg1) {
					return arg0.getValue().compareTo(arg1.getValue());
				}
			}).forEach(e ->result.put(e.getKey(),e.getValue()));
	
	     return result;
	}
}
