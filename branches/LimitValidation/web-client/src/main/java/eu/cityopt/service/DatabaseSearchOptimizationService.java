package eu.cityopt.service;

import java.text.ParseException;

import javax.script.ScriptException;
import javax.transaction.Transactional;

import eu.cityopt.DTO.ScenarioDTO;

public interface DatabaseSearchOptimizationService {

	/**
	 *  Evaluates search constrains for the chosen project/optimization set. Scenarios 
			which fulfill those requirements are stored in OptSetScenario. Their objective function 
			is evaluated and stored under OptSetScenario.value.
	 * 
	 * @param prjId
	 * 		Defines the Project to optimize
	 * @param optId
	 * 		Defines the OptimizationSet to use
	 * @param size
	 * 		Defines the maximum number of scenarios for the result list
	 * @return
	 * 		The SearchOptimizationResults, which contains a sorted list of the resulting scenarios  
	 * 		and the results of the Constraint Evaluation
	 * @throws ParseException
	 * @throws ScriptException
	 * @throws EntityNotFoundException
	 */
	public SearchOptimizationResults searchConstEval(int prjId, int optId, int size)
			throws ParseException, ScriptException, EntityNotFoundException;

	/**
	 * <li>sets the specified scenario as result of the defined optimization set</li>
	 * <li>use 0 as scenId to remove result from optimizationSet</li>
	 * @param optId optimizationSet
	 * @param scenId scenarioId or 0
	 */
	void saveSearchOptimizationResult(int optId, int scenId) throws EntityNotFoundException;

	/**
	 * get the result scenario of an optimizationset, if it has one
	 * @param optId
	 * @return result scenario or null if there is none
	 * @throws EntityNotFoundException
	 */
	ScenarioDTO getSearchOptimizationResult(int optId)
			throws EntityNotFoundException;

}