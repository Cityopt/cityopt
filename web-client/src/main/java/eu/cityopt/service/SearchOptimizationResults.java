package eu.cityopt.service;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;

/**
 * Result of the search contraint optimization.
 * Contains the evaluationResult and the resulting Scenario. 
 * resultScenario is null if there is no resulting Scenario found 
 * (e.g. evaluationResult contains no feasible scenario)
 * 
 * @author Michael
 */
public class SearchOptimizationResults {
	@Getter @Setter EvaluationResults evaluationResult;
	@Getter @Setter ScenarioDTO resultScenario;
}
