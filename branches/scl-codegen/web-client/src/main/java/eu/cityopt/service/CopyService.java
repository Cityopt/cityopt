package eu.cityopt.service;

import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.model.ExtParamValSet;

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

	/**
	 * copies the extparamvalset specified by the ID. Sets the name of the copied set to newName. 
	 * ExtParamValSet is copied on project level, together with ExtParamValSetComps and their ExtParamValues. 
	 * @return the copy result
	 * @throws EntityNotFoundException 
	 */
	ExtParamValSetDTO copyExtParamValSet(int extParamValSetId, String newName)
			throws EntityNotFoundException;

	ExtParamValSet copyExtParamValSet(ExtParamValSet epvs, String newName);

	/**
	 * copies an optimizationSet specified by the ID. The name of the copied OptimizationSet is set to newName.
	 * 
	 * @param optSetId
	 * @param newName
	 * @param copyOptSetScen - flag indicating if the search optimization results (constraint evaluation and ObtFunc value) shall be copied
	 * @return
	 * @throws EntityNotFoundException
	 */
	OptimizationSetDTO copyOptimizationSet(int optSetId, String newName,
			boolean copyOptSetScen) throws EntityNotFoundException;

	/**
	 * copies a ScenarioGenerator specified by the Id. Method does not copy ScenarioGeneratorResults (and it's dependencies) 
	 * as they are not shown in the UI. 1:n relations of ScenarioGenerator (ScenGenOptConstraint, ScenGenObjectiveFunction, 
	 * DecisionVariable, ModelParameter, AlgoParamVal) are copied
	 * @param scenGenId
	 * @param newName
	 * @return
	 * @throws EntityNotFoundException
	 */
	ScenarioGeneratorDTO copyScenarioGenerator(int scenGenId, String newName)
			throws EntityNotFoundException;
}
