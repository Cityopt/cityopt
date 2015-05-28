package eu.cityopt.service.impl;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptException;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.cityopt.DTO.ScenarioDTO;
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
	
	@Transactional
	public SearchOptimizationResults searchConstEval(int prjId, int optId) throws ParseException, ScriptException, EntityNotFoundException{   	
		Project project = projectRepository.findOne(prjId);
		
		OptimizationSet optimizationSet = optimizationSetRepository.findOne(optId);
		
		if(project == null)
			throw new EntityNotFoundException("could not find prjId: "+ prjId);
		if(optimizationSet == null)
			throw new EntityNotFoundException("could not find optId: "+ optId);
		
		if(optimizationSet.getProject().getPrjid() != prjId)
			throw new InvalidParameterException("optimization set is not part of the project" +optimizationSet);
		
		SearchOptimizationResults sor = new SearchOptimizationResults();
		EvaluationResults er = optSupport.evaluateScenarios(project, optimizationSet);
		sor.setEvaluationResult(er);

		if(!er.feasible.isEmpty()){
			ObjectiveStatus previous = null;
			int prevScenId =0;
			//Store OptSetScenarios
			for(int scenId : er.feasible.keySet()){
				OptSetScenarios oss = new OptSetScenarios();
				oss.setOptimizationset(optimizationSet);
				Scenario scen = em.getReference(Scenario.class, scenId);
				oss.setScenario(scen);
				
				ObjectiveStatus other = er.feasible.get(scenId);
				Double value = other.objectiveValues[0];				
				
				if(previous == null)
					previous = other;
				else {
					Integer compareRes = previous.compareTo(other);
					if(compareRes != null && compareRes != 0 && compareRes > 0) 
					{ 
						//other dominates previous
						previous = other;
						prevScenId = scenId;
					}
				}
				oss.setValue(String.format(Locale.ENGLISH, "%s", value));
				optSetScenariosRepository.save(oss);
			}
			
			//prevScenId is now the optimized scenario: save it
			Scenario scen = scenarioRepository.findOne(prevScenId);
			sor.setResultScenario(modelMapper.map(scen, ScenarioDTO.class));
			optimizationSet.setScenario(scen);
			optimizationSetRepository.save(optimizationSet);
		}	
		
		return sor;
	}

}
