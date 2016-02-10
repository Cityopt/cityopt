package eu.cityopt.service;

import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.model.OptConstraint;

public interface OptConstraintService extends CityOptService<OptConstraintDTO> {

	OptConstraintDTO save(OptConstraintDTO u);

	OptConstraintDTO update(OptConstraintDTO u) throws EntityNotFoundException;
	
	OptConstraintDTO findByNameAndProject(String name, int prjid);
	
	OptConstraintDTO findByNameAndScenGen(String name, int scengenid);
	
	OptConstraintDTO findByNameAndOptSet(String name, int optID);
	
	

}