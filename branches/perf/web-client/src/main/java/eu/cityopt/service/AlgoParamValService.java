package eu.cityopt.service;

import eu.cityopt.DTO.AlgoParamValDTO;

public interface AlgoParamValService extends CityOptService<AlgoParamValDTO> {

	AlgoParamValDTO save(AlgoParamValDTO u);

	AlgoParamValDTO update(AlgoParamValDTO toUpdate)
			throws EntityNotFoundException;

}