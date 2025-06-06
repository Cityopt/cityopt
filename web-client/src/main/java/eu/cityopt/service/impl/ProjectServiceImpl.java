package eu.cityopt.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptSetToOpenOptimizationSetDTOMap;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorToOpenOptimizationSetDTOMap;
import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.Metric;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.model.SimulationModel;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.CustomQueryRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.SimulationModelRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ProjectService;

@Service
@SuppressWarnings("serial")
public class ProjectServiceImpl implements ProjectService{
	private static final int PAGE_SIZE = 50;
	
	static Logger log = Logger.getLogger(ProjectServiceImpl.class);
	
	//constructor injected
	private ModelMapper modelMapper;
	@Autowired private ProjectRepository projectRepository;	
	@Autowired private CustomQueryRepository cqRepository;	
	@Autowired private SimulationModelRepository simulationModelRepository;
	@Autowired private ExtParamValSetRepository extParamValSetRepository;
	
	@Autowired private ComponentRepository componentRepository;
	
	

	@Autowired
	public ProjectServiceImpl(ModelMapper modelMapper) {
//		modelMapper = new ModelMapper();
//		modelMapper.addMappings(new ScenarioMap());
		this.modelMapper = modelMapper;
		modelMapper.addMappings(new OptSetToOpenOptimizationSetDTOMap());
		modelMapper.addMappings(new ScenarioGeneratorToOpenOptimizationSetDTOMap());
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		Project project = projectRepository.findOne(id);
		
		if(project == null) {
			throw new EntityNotFoundException();
		}

		SimulationModel sm = project.getSimulationmodel();
		if(sm!=null && sm.getProjects().size() == 1){
			//this is the simulationmodel's last project, delete the model
			projectRepository.delete(project.getPrjid());
			simulationModelRepository.delete(sm.getModelid());
		}
		else
			projectRepository.delete(project.getPrjid());
	}
	
	@Transactional
	public void deleteAll() {
		projectRepository.deleteAll();
	}
	
	@Transactional
	public void deleteWR(int id) throws EntityNotFoundException {
		
		if(projectRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		projectRepository.delete(id);
	}

	@Transactional(readOnly = true)
	public List<ProjectDTO> findAll() {
		List<Project> projects = projectRepository.findAll(new Sort(Sort.Direction.ASC, SortBy.name.toString()));
		
		List<ProjectDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectDTO>>() {}.getType());
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectDTO> findAll(SortBy col) {
		List<Project> projects = projectRepository.findAll(new Sort(Sort.Direction.ASC, col.toString()));
		
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
	public ProjectDTO findByID(int id) throws EntityNotFoundException {
		Project item = projectRepository.findOne(id);
		if(item == null)
			throw new EntityNotFoundException();
		ProjectDTO itemDTO = modelMapper.map(item, ProjectDTO.class);
		return itemDTO;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectDTO> findByNameContaining(String name) {
		List<Project> projects = projectRepository.findByNameContainingIgnoreCase(name,new Sort(Direction.ASC,SortBy.name.toString()));
		
		List<ProjectDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectDTO>>() {}.getType());
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public ProjectDTO findByName(String name) {
		Project projects = projectRepository.findByName(name);
		if(projects != null)
			return modelMapper.map(projects, ProjectDTO.class);
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<ComponentDTO> getComponents(int prjid) {
		Project item = projectRepository.findOne(prjid);
		List<Component> components = item.getComponents(); 
		return modelMapper.map(components, new TypeToken<List<ComponentDTO>>() {}.getType());
	}
	
	@Override
	public Page<ComponentDTO> getComponents(int prjid, int pageIndex) {
		
		PageRequest request =
	            new PageRequest(pageIndex,PAGE_SIZE);
		
		Page<Component> components = componentRepository.findByProject(prjid, request);				 
		return modelMapper.map(components, new TypeToken<Page<ComponentDTO>>() {}.getType());
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer getDefaultExtParamSetId(int prjid){
	    Project p = projectRepository.findOne(prjid);
	    if(p == null)
	        return null;
	    ExtParamValSet x = p.getDefaultextparamvalset();
	    return x != null ? x.getExtparamvalsetid() : null;
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
	
	@Override
	@Transactional(readOnly = true)
	public List<ExtParamValSetDTO> getExtParamValSets(int prjid) {
		List<ExtParamValSet> epvsList = extParamValSetRepository.findByProject(prjid);
		return modelMapper.map(epvsList, new TypeToken<List<ExtParamValSetDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public Set<MetricDTO> getMetrics(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Metric> metrics = item.getMetrics(); 
		return modelMapper.map(metrics, new TypeToken<LinkedHashSet<MetricDTO>>() {}.getType());
	}

	@Transactional(readOnly = true)
	@Override
	public Set<ObjectiveFunctionDTO> getObjectiveFunctions(int prjid) throws EntityNotFoundException {
		Project p = projectRepository.findOne(prjid);
		
		if(p == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(p.getObjectivefunctions(), new TypeToken<Set<ObjectiveFunctionDTO>>() {}.getType());
	}

	@Transactional(readOnly = true)
	@Override
	public Set<OptConstraintDTO> getOptConstraints(int prjid) throws EntityNotFoundException {
		Project p = projectRepository.findOne(prjid);
		if (p == null) {
			throw new EntityNotFoundException();
		}
		return modelMapper.map(p.getOptconstraints(), new TypeToken<Set<OptConstraintDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public Set<ScenarioDTO> getScenarios(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scenarios = item.getScenarios();
		return modelMapper.map(scenarios, new TypeToken<LinkedHashSet<ScenarioDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	@Override
	public Set<OpenOptimizationSetDTO> getSearchAndGAOptimizationSets(int prjid) 
			throws EntityNotFoundException {
		Project p = projectRepository.findOne(prjid);
		
		if(p == null) {
			throw new EntityNotFoundException();
		}
		
		Set<OptimizationSet> osSet = p.getOptimizationsets();
		Set<ScenarioGenerator> sgSet = p.getScenariogenerators();
		Set<OpenOptimizationSetDTO> osSetDTO = modelMapper.map(osSet, 
				new TypeToken<LinkedHashSet<OpenOptimizationSetDTO>>() {}.getType());
		osSetDTO.addAll(modelMapper.map(sgSet, 
				new TypeToken<LinkedHashSet<OpenOptimizationSetDTO>>() {}.getType()));

		return osSetDTO;
	}
	
	@Transactional(readOnly = true)
	@Override
	public Set<OptimizationSetDTO> getSearchOptimizationSets(int prjid) throws EntityNotFoundException {
		Project p = projectRepository.findOne(prjid);
		
		if(p == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(p.getOptimizationsets(), new TypeToken<LinkedHashSet<OptimizationSetDTO>>() {}.getType());
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer getSimulationmodelId(int prjid) {
	    Project p = projectRepository.findOne(prjid);
	    if(p == null)
	        return null;
	    SimulationModel m = p.getSimulationmodel();
	    return m != null ? m.getModelid() : null;
	}

	@Transactional
	public ProjectDTO save(ProjectDTO projectDTO, Integer simulationModelId, Integer extParamValSetId) {
		Project result = modelMapper.map(projectDTO, Project.class);
		
		if(simulationModelId!=null)
		{
			SimulationModel sm = simulationModelRepository.findOne(simulationModelId);
			result.setSimulationmodel(sm);
		}
		
		if(extParamValSetId!=null)
		{
			ExtParamValSet epvs = extParamValSetRepository.findOne(extParamValSetId);
			result.setDefaultextparamvalset(epvs);
		}
		
		result = projectRepository.save(result);
		projectDTO = modelMapper.map(result, ProjectDTO.class);
		return projectDTO;
	}

	@Transactional
	public void setScenarios(int prjid, Set<ScenarioDTO> scenarios) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scen = modelMapper.map(scenarios, new TypeToken<Set<Scenario>>() {}.getType());
		
		item.setScenarios(scen);
		projectRepository.saveAndFlush(item);
	}

	@Transactional
	public ProjectDTO update(ProjectDTO toUpdate, Integer simulationModelId, Integer extParamValSetId) throws EntityNotFoundException {
		
		if(projectRepository.findOne(toUpdate.getPrjid()) == null) {
			throw new EntityNotFoundException();
		}
		return save(toUpdate, simulationModelId, extParamValSetId);
	}
}


