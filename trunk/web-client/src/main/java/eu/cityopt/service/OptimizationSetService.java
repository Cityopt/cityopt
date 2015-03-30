package eu.cityopt.service;

import eu.cityopt.DTO.OptimizationSetDTO;

public interface OptimizationSetService extends CityOptService<OptimizationSetDTO>{

	OptimizationSetDTO update(OptimizationSetDTO toUpdate)
			throws EntityNotFoundException;

	OptimizationSetDTO save(OptimizationSetDTO u);

}