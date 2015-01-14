package eu.cityopt.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioMap;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ProjectRepository;

@Service("projectServiceImpl")
public class ProjectServiceImpl{
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ProjectRepository projectRepository;

	public ProjectServiceImpl() {
//		modelMapper = new ModelMapper();
//		modelMapper.addMappings(new ScenarioMap());
	}
	
	public ProjectRepository getProjectRepository() {
		return projectRepository;
	}

	public void setProjectRepository(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	public List<ProjectDTO> findAll() {
		List<Project> projects = projectRepository.findAll();
		List<ProjectDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectDTO>>() {}.getType());
		return result;
	}
	
	public List<ProjectScenariosDTO> findAllWithScenarios() {
		List<Project> projects = projectRepository.findAll();
		List<ProjectScenariosDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectScenariosDTO>>() {}.getType());
		return result;
	}

	@Transactional
	public ProjectDTO save(ProjectDTO projectDTO) {
		Project project = new Project();
		Project result = modelMapper.map(projectDTO, Project.class);
		result = projectRepository.save(project);
		modelMapper.map(result, projectDTO);
		return projectDTO;
	}

	@Transactional
	public void deleteAll() {
		projectRepository.deleteAll();
	}
	
	@Transactional
	public void delete(Integer id) throws EntityNotFoundException {
		
		if(projectRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		projectRepository.delete(id);
	}
	
	@Transactional
	public ProjectDTO update(ProjectDTO toUpdate) throws EntityNotFoundException {
		
		if(projectRepository.findOne(toUpdate.getPrjid()) == null) {
			throw new EntityNotFoundException();
		}
		return save(toUpdate);
	}
	
	public ProjectDTO findByID(Integer id) {
		Project item = projectRepository.findOne(id);
		ProjectDTO itemDTO = modelMapper.map(item, ProjectDTO.class);
		return itemDTO;
	}
	
	public Set<ScenarioDTO> getScenarios(Integer prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scenarios = item.getScenarios(); 
		return modelMapper.map(scenarios, new TypeToken<Set<ScenarioDTO>>() {}.getType());
	}
	
	@Transactional
	public void setScenarios(Integer prjid, Set<ScenarioDTO> scenarios) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scen = modelMapper.map(scenarios, new TypeToken<Set<Scenario>>() {}.getType());
		
		item.setScenarios(scen);
		projectRepository.saveAndFlush(item);
	}
	
	public Set<ComponentDTO> getComponents(Integer prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scenarios = item.getScenarios(); 
		return modelMapper.map(scenarios, new TypeToken<Set<ComponentDTO>>() {}.getType());
	}
}


