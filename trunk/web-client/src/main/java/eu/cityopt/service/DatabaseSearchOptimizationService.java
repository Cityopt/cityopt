package eu.cityopt.service;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.cityopt.model.OptSetScenarios;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.OptSetScenariosRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.service.OptimisationSupport;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;

@Service
public class DatabaseSearchOptimizationService {
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	OptimizationSetRepository optimizationSetRepository;
	
	@Autowired
	OptSetScenariosRepository optSetScenariosRepository;
	
	@Autowired
	OptimisationSupport optSupport;
	
	@PersistenceContext
	EntityManager em;
	
	@Transactional
	public EvaluationResults searchConstEval(int prjId, int optId) throws ParseException, ScriptException, EntityNotFoundException{   	
		Project project = projectRepository.findOne(prjId);
		
		OptimizationSet optimizationSet = optimizationSetRepository.findOne(optId);
		
		if(project == null)
			throw new EntityNotFoundException("could not find prjId: "+ prjId);
		if(optimizationSet == null)
			throw new EntityNotFoundException("could not find optId: "+ optId);
		
		if(optimizationSet.getPrjid() != prjId)
			throw new InvalidParameterException("optimization set is not part of the project" +optimizationSet);
		
		EvaluationResults er = optSupport.evaluateScenarios(project, optimizationSet);
		
//		boolean isMax = optimizationSet.getObjectivefunction().getIsmaximise();
		
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
				//TODO possible nullpointer exception?
				Double value = other.objectiveValues[0];				
				
				if(previous == null)
					previous = other;
				else {
					Integer compareRes = previous.compareTo(other);
					if(compareRes != null && compareRes != 0 && compareRes > 0) 
					{ //other dominates
						previous = other;
						prevScenId = scenId;
					}
//					else if(compareRes > 0){ //this dominates
//						if(!isMax){ //-->minimize: store the smaller one
//							prevScenId = scenId;
//							previous = other;
//						}
//					}else{
//						if(isMax){
//							prevScenId = scenId;
//							previous = other;
//						}
//					}
				}
				oss.setValue(String.format(Locale.ENGLISH, "%s", value));
				optSetScenariosRepository.save(oss);
			}
			//prevScenId is now the optimized scenario: save it
			Scenario scen = em.getReference(Scenario.class, prevScenId);
			optimizationSet.setScenario(scen);
			optimizationSetRepository.save(optimizationSet);
		}else{
			//no Scenario is feasible - what now?
		}	
		
		return er;
		//System.out.println(er);
	}
	
	
	
}
