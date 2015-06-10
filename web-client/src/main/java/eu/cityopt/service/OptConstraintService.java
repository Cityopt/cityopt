package eu.cityopt.service;

import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.model.OptConstraint;

public interface OptConstraintService extends CityOptService<OptConstraintDTO> {

	OptConstraintDTO save(OptConstraintDTO u);

	OptConstraintDTO update(OptConstraintDTO u) throws EntityNotFoundException;

}