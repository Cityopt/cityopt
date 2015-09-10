package eu.cityopt.service;

import eu.cityopt.DTO.DecisionVariableDTO;

public interface DecisionVariableService extends CityOptService<DecisionVariableDTO>{

	DecisionVariableDTO save(DecisionVariableDTO u);

	DecisionVariableDTO update(DecisionVariableDTO toUpdate)
			throws EntityNotFoundException;

}