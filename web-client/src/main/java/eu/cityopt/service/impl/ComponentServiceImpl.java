package eu.cityopt.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.service.ComponentService;
import eu.cityopt.service.EntityNotFoundException;

@Service("ComponentService")
public class ComponentServiceImpl implements ComponentService {
	private static final int PAGE_SIZE = 50;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ComponentRepository componentRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Transactional(readOnly = true)
	public List<ComponentDTO> findAll() {
		return modelMapper.map(componentRepository.findAll(), 
				new TypeToken<List<ComponentDTO>>() {}.getType());
	}

	@Transactional(readOnly = true)
	public List<ComponentDTO> findByName(String name) {
		List<Component> components = componentRepository.findByNameContaining(name);
		List<ComponentDTO> result 
			= modelMapper.map(components, new TypeToken<List<ComponentDTO>>() {}.getType());
		return result;
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
	
	@Transactional(readOnly = true)
	public ComponentDTO findByID(int id) throws EntityNotFoundException {
		if(componentRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return  modelMapper.map(componentRepository.findOne(id), ComponentDTO.class);
	}
	
	@Transactional(readOnly = true)
	public List<InputParameterDTO> getInputParameters(int componentId)
	{
		Component comp = componentRepository.findOne(componentId);
		List<InputParameter> inputParamVals = comp.getInputparameters();
		return modelMapper.map(inputParamVals, new TypeToken<List<InputParameterDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public List<OutputVariableDTO> getOutputVariables(int componentId)
	{
		Component comp = componentRepository.findOne(componentId);
		List<OutputVariable> inputParamVals = comp.getOutputvariables();
		return modelMapper.map(inputParamVals, new TypeToken<List<OutputVariableDTO>>() {}.getType());
	}

	@Override
	public Page<ComponentDTO> findByProject(int prjid,int pageIndex) {
		PageRequest request =
	            new PageRequest(pageIndex,PAGE_SIZE);		
		
		Page<Component> components = componentRepository.findByProject(prjid, request);		
		
		return modelMapper.map(components, new TypeToken<Page<ComponentDTO>>() {}.getType());		
	}
	
}
