package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;

public interface ScenarioGeneratorService extends CityOptService<ScenarioGeneratorDTO>{

	ScenarioGeneratorDTO update(ScenarioGeneratorDTO toUpdate)
			throws EntityNotFoundException;

	ScenarioGeneratorDTO save(ScenarioGeneratorDTO u);

	List<AlgoParamDTO> getAlgoParams(int scenGenId)
			throws EntityNotFoundException;

	ObjectiveFunctionDTO addObjectiveFunction(int scenGenId,
			ObjectiveFunctionDTO obtFuncDTO) throws EntityNotFoundException;

	OptConstraintDTO addSearchConstraint(int scenGenId, OptConstraintDTO ocDTO)
			throws EntityNotFoundException;

}