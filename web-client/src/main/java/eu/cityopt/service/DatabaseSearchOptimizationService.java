package eu.cityopt.service;

import java.text.ParseException;

import javax.script.ScriptException;
import javax.transaction.Transactional;

public interface DatabaseSearchOptimizationService {

	/**
	 *  Evaluates search constrains for the chosen project/optimization set. Scenarios 
			which fulfill those requirements are stored in OptSetScenario. Their objective function 
			is evaluated and stored under OptSetScenario.value. The result scenario is stored in OptimizationSet (Just one Scenario)
	 * 
	 * @param prjId
	 * 		Defines the Project to optimize
	 * @param optId
	 * 		Defines the OptimizationSet to use
	 * @return
	 * 		The SearchOptimizationResults, which contain the resulting scenario (if not null) 
	 * 		and the results of the Constraint Evaluation
	 * @throws ParseException
	 * @throws ScriptException
	 * @throws EntityNotFoundException
	 */
	public SearchOptimizationResults searchConstEval(int prjId, int optId)
			throws ParseException, ScriptException, EntityNotFoundException;

}