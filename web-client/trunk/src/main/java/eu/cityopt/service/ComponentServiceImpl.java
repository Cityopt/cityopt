package eu.cityopt.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.model.Component;
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
	public void delete(Integer id) throws EntityNotFoundException {
		
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
	
	public ComponentDTO findByID(Integer id) throws EntityNotFoundException {
		if(componentRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return  modelMapper.map(componentRepository.findOne(id), ComponentDTO.class);
	}
	
}
