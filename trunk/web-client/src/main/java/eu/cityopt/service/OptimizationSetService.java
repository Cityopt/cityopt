package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptimizationSetDTO;

public interface OptimizationSetService extends CityOptService<OptimizationSetDTO>{

	OptimizationSetDTO update(OptimizationSetDTO toUpdate)
			throws EntityNotFoundException;

	OptimizationSetDTO save(OptimizationSetDTO u);

	List<OptConstraintDTO> getSearchConstraints(int optimizationSetId)
			throws EntityNotFoundException;

}