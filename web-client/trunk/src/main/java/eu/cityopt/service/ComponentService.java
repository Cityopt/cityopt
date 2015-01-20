package eu.cityopt.service;

import java.util.Set;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.OutputVariableDTO;

public interface ComponentService extends CityOptService<ComponentDTO>{
	ComponentDTO save(ComponentDTO u, int prjid);
	
	ComponentDTO update(ComponentDTO toUpdate, int prjid) throws EntityNotFoundException;
	
	Set<InputParameterDTO> getInputParameters(int componentId);
	
	Set<OutputVariableDTO> getOutputVariables(int componentId);
}