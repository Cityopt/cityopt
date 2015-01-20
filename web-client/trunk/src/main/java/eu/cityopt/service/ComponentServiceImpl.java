package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ProjectRepository;

@Service("ComponentService")
public class ComponentServiceImpl implements ComponentService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ComponentRepository componentRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	public List<ComponentDTO> findAll() {
		return modelMapper.map(componentRepository.findAll(), 
				new TypeToken<List<ComponentDTO>>() {}.getType());
	}

	@Transactional
	public ComponentDTO save(ComponentDTO u, int prjid) {
		Component component = modelMapper.map(u, Component.class);
		Project p = projectRepository.findOne(prjid);
		component.setProject(p);
		component = componentRepository.save(component);
		return modelMapper.map(component, ComponentDTO.class);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(componentRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		componentRepository.delete(id);
	}
	
	@Transactional
	public ComponentDTO update(ComponentDTO toUpdate, int prjid) throws EntityNotFoundException {
		
		if(componentRepository.findOne(toUpdate.getComponentid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate, prjid);
	}
	
	public ComponentDTO findByID(int id) throws EntityNotFoundException {
		if(componentRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return  modelMapper.map(componentRepository.findOne(id), ComponentDTO.class);
	}
	
	public Set<InputParameterDTO> getInputParameters(int componentId)
	{
		Component comp = componentRepository.findOne(componentId);
		Set<InputParameter> inputParamVals = comp.getInputparameters();
		return modelMapper.map(inputParamVals, new TypeToken<Set<InputParameterDTO>>() {}.getType());
	}
	
	public Set<OutputVariableDTO> getOutputVariables(int componentId)
	{
		Component comp = componentRepository.findOne(componentId);
		Set<OutputVariable> inputParamVals = comp.getOutputvariables();
		return modelMapper.map(inputParamVals, new TypeToken<Set<OutputVariableDTO>>() {}.getType());
	}
	
}
