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
import eu.cityopt.service.OptSetScenariosService;
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
	private ModelMapper modelMapper;

	@Autowired
	private OptSetScenariosService optSetScenariosService;

	@PersistenceContext
	private EntityManager em;

	@Override
	public SearchOptimizationResults searchConstEval(
	        int prjId, int optId, int size)
	                throws ParseException, ScriptException,
	                        EntityNotFoundException {
		SearchOptimizationResults sor = optSupport.searchOptimization(
		        prjId, optId, size);
		optSupport.saveMetricVals(sor.getEvaluationResult());
                optSetScenariosService.saveEvaluationResults(optId, sor);

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

}
