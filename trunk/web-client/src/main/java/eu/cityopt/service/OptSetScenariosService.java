package eu.cityopt.service;

import eu.cityopt.DTO.OptSetScenariosDTO;

public interface OptSetScenariosService extends CityOptService<OptSetScenariosDTO> {

	OptSetScenariosDTO save(OptSetScenariosDTO u);

	OptSetScenariosDTO update(OptSetScenariosDTO toUpdate)
			throws EntityNotFoundException;

}
