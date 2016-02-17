package eu.cityopt.service;

import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.OptConstraintDTO;

public interface DecisionVariableService extends CityOptService<DecisionVariableDTO>{

	DecisionVariableDTO save(DecisionVariableDTO u);

	DecisionVariableDTO update(DecisionVariableDTO toUpdate)
			throws EntityNotFoundException;
	
	DecisionVariableDTO findByNameAndScenGen(String name, int scengenid);

}