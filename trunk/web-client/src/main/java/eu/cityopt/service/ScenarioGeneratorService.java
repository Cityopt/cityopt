package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;

public interface ScenarioGeneratorService extends CityOptService<ScenarioGeneratorDTO>{

	ScenarioGeneratorDTO update(ScenarioGeneratorDTO toUpdate)
			throws EntityNotFoundException;

	ScenarioGeneratorDTO save(ScenarioGeneratorDTO u);

	List<AlgoParamDTO> getAlgoParamVals(int scenGenId)
			throws EntityNotFoundException;

}