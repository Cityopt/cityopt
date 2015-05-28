package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;

public interface ScenarioGeneratorService extends CityOptService<ScenarioGeneratorDTO>{

	ScenarioGeneratorDTO update(ScenarioGeneratorDTO toUpdate)
			throws EntityNotFoundException;

	ScenarioGeneratorDTO save(ScenarioGeneratorDTO u);

	List<AlgoParamDTO> getAlgoParams(int scenGenId)
			throws EntityNotFoundException;
	
	List<ModelParameterDTO> getModelParameters(int scenGenId)
			throws EntityNotFoundException;

	List<ObjectiveFunctionDTO> getObjectiveFunctions(int scenGenId) throws EntityNotFoundException;
	
	ObjectiveFunctionDTO addObjectiveFunction(int scenGenId,
			ObjectiveFunctionDTO obtFuncDTO) throws EntityNotFoundException;
	
	void removeObjectiveFunction(int scenGenId, int objectiveFunctionId) throws EntityNotFoundException;

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

}