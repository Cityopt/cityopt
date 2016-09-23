package eu.cityopt.service;

import java.util.List;
import java.util.Map;

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.AlgoParamValDTO;
import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.ScenarioGeneratorSimpleDTO;

public interface ScenarioGeneratorService extends CityOptService<ScenarioGeneratorDTO>{

	ScenarioGeneratorDTO update(ScenarioGeneratorDTO toUpdate)
			throws EntityNotFoundException;

	ScenarioGeneratorDTO save(ScenarioGeneratorDTO u);
	
	ScenarioGeneratorDTO findByName(String name,int prjid);
	List<ScenarioGeneratorDTO> findByName(String name);

	List<AlgoParamDTO> getAlgoParams(int scenGenId)
			throws EntityNotFoundException;

	List<AlgoParamValDTO> getAlgoParamVals(int scenGenId)
			throws EntityNotFoundException;

	List<AlgoParamValDTO> getOrCreateAlgoParamVals(int scengenid)
			throws EntityNotFoundException;

	void setAlgoParamVals(int scenGenId, Map<Integer, String> valueByParamId)
			throws EntityNotFoundException;

	List<ModelParameterDTO> getModelParameters(int scenGenId)
			throws EntityNotFoundException;

	void setModelParameters(int scengenid, List<ModelParameterDTO> modelParams)
			throws EntityNotFoundException;

	List<ObjectiveFunctionDTO> getObjectiveFunctions(int scenGenId)
			throws EntityNotFoundException;
	
	ObjectiveFunctionDTO addObjectiveFunction(int scenGenId,
			ObjectiveFunctionDTO obtFuncDTO) throws EntityNotFoundException;
	
	void removeObjectiveFunction(int scenGenId, int objectiveFunctionId)
			throws EntityNotFoundException;

	List<OptConstraintDTO> getOptConstraints(int scenGenId)
			throws EntityNotFoundException;

	/**
	 * adds an OptConstraint to a ScenarioGenerator. Creates the OptConstraint if not existing
	 * @param scenGenId
	 * @param optConstraintId
	 * @throws EntityNotFoundException
	 */
	OptConstraintDTO addOptConstraint(int scenGenId, OptConstraintDTO ocDTO)
			throws EntityNotFoundException;

	/**
	 * removes an OptConstraint from a ScenarioGenerator. Does not delete the OptConstraint
	 * @param scenGenId
	 * @param optConstraintId
	 * @throws EntityNotFoundException
	 */
	void removeOptConstraint(int scenGenId, int optConstraintId)
			throws EntityNotFoundException;

	List<DecisionVariableDTO> getDecisionVariables(int scengenid) throws EntityNotFoundException;

	/**
	 * Adds a new decision variable to a scenario generator.
	 */
	DecisionVariableDTO addDecisionVariable(int scenGenId,
			DecisionVariableDTO decVarDTO) throws EntityNotFoundException;

	void removeDecisionVariable(int scenGenId, int decVarId) throws EntityNotFoundException;

	void setDecisionVariables(int scenGenId, List<DecisionVariableDTO> variables)
			throws EntityNotFoundException;

	/**
	 * Reads model parameters and determines their types and grouping. 
	 */
	ModelParameterGrouping getModelParameterGrouping(int scenGenId)
			throws EntityNotFoundException;

	/**
	 * Saves any changes to model parameters and their grouping. In addition to
	 * model parameters, some decision variables of the scenario generator are
	 * also updated: specifically the variables that are associated with groups,
	 * and the variables that are directly linked to model parameters.
	 */
	void setModelParameterGrouping(int scenGenId,
			ModelParameterGrouping grouping) throws EntityNotFoundException;

	/**
	 * Saves only the decision variables of the model parameter grouping.
	 */
	void updateDecisionVariables(int scenGenId,
			ModelParameterGrouping grouping) throws EntityNotFoundException;

	/**
	 * Creates and initializes a ScenarioGenerator with the given name.
	 * The default algorithm is used.
	 * @param projectId
	 * @param name
	 * @return
	 */
	ScenarioGeneratorDTO create(int projectId, String name);

	ScenarioGeneratorDTO update(int scenGenId, String name, Integer algorithmId)
			throws EntityNotFoundException;
	
	ScenarioGeneratorDTO update(int scenGenId, String name,String description, Integer algorithmId)
			throws EntityNotFoundException;

	/** Converts ScenarioGeneratorDTO to ScenarioGeneratorSimpleDTO. */
    public static ScenarioGeneratorSimpleDTO convertDTO(
			ScenarioGeneratorDTO scenGen) {
    	ScenarioGeneratorSimpleDTO s = new ScenarioGeneratorSimpleDTO();
    	s.setVersion(scenGen.getVersion());
    	s.setScengenid(scenGen.getScengenid());
		return s;
	}
}