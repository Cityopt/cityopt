package eu.cityopt.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.Metric;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ProjectRepository;

@Service
public class ProjectServiceImpl implements ProjectService{
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ProjectRepository projectRepository;

	public ProjectServiceImpl() {
//		modelMapper = new ModelMapper();
//		modelMapper.addMappings(new ScenarioMap());
	}

	@Transactional(readOnly = true)
	public List<ProjectDTO> findAll() {
		List<Project> projects = projectRepository.findAll();
		List<ProjectDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectDTO>>() {}.getType());
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectScenariosDTO> findAllWithScenarios() {
		List<Project> projects = projectRepository.findAllWithScenarios();
		List<ProjectScenariosDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectScenariosDTO>>() {}.getType());
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectDTO> findByName(String name) {
		List<Project> projects = projectRepository.findByName(name);
		List<ProjectDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectDTO>>() {}.getType());
		return result;
	}

	@Transactional
	public ProjectDTO save(ProjectDTO projectDTO) {
		Project result = modelMapper.map(projectDTO, Project.class);
		result = projectRepository.save(result);
		modelMapper.map(result, projectDTO);
		return projectDTO;
	}

	@Transactional
	public void deleteAll() {
		projectRepository.deleteAll();
	}
	
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
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
	
	@Transactional(readOnly = true)
	public ProjectDTO findByID(int id) {
		Project item = projectRepository.findOne(id);
		ProjectDTO itemDTO = modelMapper.map(item, ProjectDTO.class);
		return itemDTO;
	}
	
	@Transactional(readOnly = true)
	public Set<ScenarioDTO> getScenarios(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scenarios = item.getScenarios(); 
		return modelMapper.map(scenarios, new TypeToken<Set<ScenarioDTO>>() {}.getType());
	}
	
	@Transactional
	public void setScenarios(int prjid, Set<ScenarioDTO> scenarios) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scen = modelMapper.map(scenarios, new TypeToken<Set<Scenario>>() {}.getType());
		
		item.setScenarios(scen);
		projectRepository.saveAndFlush(item);
	}
	
	@Transactional(readOnly = true)
	public List<ComponentDTO> getComponents(int prjid) {
		Project item = projectRepository.findOne(prjid);
		List<Component> components = item.getComponents(); 
		return modelMapper.map(components, new TypeToken<List<ComponentDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public Set<ExtParamDTO> getExtParams(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<ExtParam> extParams = item.getExtparams(); 
		return modelMapper.map(extParams, new TypeToken<Set<ExtParamDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public Set<ExtParamValDTO> getExtParamVals(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<ExtParam> extParams = item.getExtparams(); 
		Set<ExtParamVal> extParamVals = new HashSet<ExtParamVal>();
		for(Iterator<ExtParam> i = extParams.iterator(); i.hasNext();){
			ExtParam ep = i.next();
			extParamVals.addAll(ep.getExtparamvals());
		}
		return modelMapper.map(extParamVals, new TypeToken<Set<ExtParamValDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public Set<MetricDTO> getMetrics(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Metric> metrics = item.getMetrics(); 
		return modelMapper.map(metrics, new TypeToken<Set<MetricDTO>>() {}.getType());
	}
}


