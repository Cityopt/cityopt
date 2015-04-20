package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.AlgorithmDTO;
import eu.cityopt.model.AlgoParam;
import eu.cityopt.model.Algorithm;

public interface AlgorithmService extends CityOptService<AlgorithmDTO> {

	List<AlgoParamDTO> getAlgoParams(int algorithmId)
			throws EntityNotFoundException;

}