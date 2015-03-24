package eu.cityopt.service;

import eu.cityopt.DTO.ScenarioDTO;

public interface CopyService {
	public ScenarioDTO copyScenario (int id, String name, boolean copyInputParamVals, 
			boolean copyMetricValues, boolean addToOptimizationSet, boolean copySimulationResults) throws 
			EntityNotFoundException;
}
