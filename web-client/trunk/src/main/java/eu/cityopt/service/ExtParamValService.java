package eu.cityopt.service;

import eu.cityopt.DTO.ExtParamValDTO;

public interface ExtParamValService extends CityOptService<ExtParamValDTO> {

	public ExtParamValDTO save(ExtParamValDTO u);	

	ExtParamValDTO update(ExtParamValDTO toUpdate) throws EntityNotFoundException;
	
}