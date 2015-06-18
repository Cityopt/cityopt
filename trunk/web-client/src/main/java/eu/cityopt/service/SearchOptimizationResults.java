package eu.cityopt.service;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;

/**
 * Result of the search contraint optimization.
 * Contains the evaluationResult and the resulting Scenarios (sorted descending, starting with the "most feasible"). 
 * resultScenarios is empty if no scenario is found 
 * (e.g. evaluationResult contains no feasible scenario)
 * 
 * @author Michael
 */
public class SearchOptimizationResults {
	@Getter @Setter EvaluationResults evaluationResult;
	public List<ScenarioWithObjFuncValueDTO> resultScenarios;
}
