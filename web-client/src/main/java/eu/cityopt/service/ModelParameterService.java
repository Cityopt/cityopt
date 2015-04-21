package eu.cityopt.service;

import eu.cityopt.DTO.ModelParameterDTO;

public interface ModelParameterService extends CityOptService<ModelParameterDTO> {

	ModelParameterDTO save(ModelParameterDTO u);

	ModelParameterDTO update(ModelParameterDTO toUpdate)
			throws EntityNotFoundException;

}