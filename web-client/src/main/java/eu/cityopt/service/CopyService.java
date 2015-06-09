package eu.cityopt.service;

import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;

/**
 * 
 * Service functions for deep copying of entities. 
 * Copy results are directly stored in the database
 * 
 * @author Michael
 */
public interface CopyService {
	/**
	 * @param id - id of source scenario
	 * @param name - new name for the copied scenario
	 * @param copyInputParamVals - flag indicating if InputParamValues shall be copied
	 * @param copyMetricValues - flag indicating if metric values should be copied
	 * @param addToOptimizationSet - flag indicating if the copied scenario should 
	 * be added to the source's optimization set
	 * @param copySimulationResults - flag indicating if simulation results should 
	 * be copied (might be time consuming)
	 * @return copied scenario
	 * @throws EntityNotFoundException
	 */
	public ScenarioDTO copyScenario (int id, String name, boolean copyInputParamVals, 
			boolean copyMetricValues, boolean addToOptimizationSet, boolean copySimulationResults) throws 
			EntityNotFoundException;

	/**
	 * @param id - id of the source project
	 * @param name - name for the copied project
	 * @return
	 * @throws EntityNotFoundException
	 */
	ProjectDTO copyProject(int id, String name) throws EntityNotFoundException;
	
	/**
	 * @param id - id of the source metric
	 * @param name - name for the copied metric
	 * @return
	 * @throws EntityNotFoundException
	 */
	MetricDTO copyMetric(int id, String name) throws EntityNotFoundException;
}
