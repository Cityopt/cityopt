package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.OutputVariable;

public interface ComponentService extends CityOptService<ComponentDTO>{
	ComponentDTO save(ComponentDTO u, int prjid);
	
	ComponentDTO update(ComponentDTO toUpdate, int prjid) throws EntityNotFoundException;
	
	List<InputParameterDTO> getInputParameters(int componentId);
	
	List<OutputVariableDTO> getOutputVariables(int componentId);
	
	List<ComponentDTO> findByName(String name);
	
	Page<ComponentDTO> findByProject(int prjid,int pageIndex);
}