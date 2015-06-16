package eu.cityopt.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import eu.cityopt.DTO.OptSetToOpenOptimizationSetDTOMap;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorToOpenOptimizationSetDTOMap;
import eu.cityopt.DTO.SimulationModelDTO;
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
import eu.cityopt.repository.CustomQueryRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.SimulationModelRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService{
	
	//constructor injected
	private ModelMapper modelMapper;
	
	@Autowired private ProjectRepository projectRepository;
	@Autowired private CustomQueryRepository cqRepository;	
	@Autowired private SimulationModelRepository simulationModelRepository;	
	@Autowired private ExtParamValSetRepository extParamValSetRepository;
	@PersistenceContext EntityManager em;
	
	static Logger log = Logger.getLogger(ProjectServiceImpl.class);

	@Autowired
	public ProjectServiceImpl(ModelMapper modelMapper) {
//		modelMapper = new ModelMapper();
//		modelMapper.addMappings(new ScenarioMap());
		this.modelMapper = modelMapper;
		modelMapper.addMappings(new OptSetToOpenOptimizationSetDTOMap());
		modelMapper.addMappings(new ScenarioGeneratorToOpenOptimizationSetDTOMap());
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
	public List<ProjectDTO> findByNameContaining(String name) {
		List<Project> projects = projectRepository.findByName(name);
		List<ProjectDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectDTO>>() {}.getType());
		return result;
	}

	@Transactional
	public ProjectDTO save(ProjectDTO projectDTO, int simulationModelId, int extParamValSetId) {
		Project result = modelMapper.map(projectDTO, Project.class);
		SimulationModel sm = simulationModelRepository.findOne(simulationModelId);
		ExtParamValSet epvs = extParamValSetRepository.findOne(extParamValSetId);
		result.setSimulationmodel(sm);
		result.setDefaultextparamvalset(epvs);
		result = projectRepository.save(result);
		projectDTO = modelMapper.map(result, ProjectDTO.class);
		return projectDTO;
	}

	@Transactional
	public void deleteAll() {
		projectRepository.deleteAll();
	}
	
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		Project project = projectRepository.findOne(id);
		
		if(project == null) {
			throw new EntityNotFoundException();
		}

		projectRepository.delete(project.getPrjid());
	}
	
	@Transactional
	public void deleteWR(int id) throws EntityNotFoundException {
		
		if(projectRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		projectRepository.delete(id);
	}
	
	@Transactional
	public ProjectDTO update(ProjectDTO toUpdate, int simulationModelId, int extParamValSetId) throws EntityNotFoundException {
		
		if(projectRepository.findOne(toUpdate.getPrjid()) == null) {
			throw new EntityNotFoundException();
		}
		return save(toUpdate, simulationModelId, extParamValSetId);
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

	@Override
	@Transactional(readOnly = true)
	public List<ExtParamValSetDTO> getExtParamValSets(int prjid) {
		List<ExtParamValSet> epvsList = extParamValSetRepository.findByProject(prjid);
		return modelMapper.map(epvsList, new TypeToken<List<ExtParamValSetDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	@Override
	public Set<OptimizationSetDTO> getSearchOptimizationSets(int prjid) throws EntityNotFoundException {
		Project p = projectRepository.findOne(prjid);
		
		if(p == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(p.getOptimizationsets(), new TypeToken<Set<OptimizationSetDTO>>() {}.getType());
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
				new TypeToken<Set<OpenOptimizationSetDTO>>() {}.getType());
		osSetDTO.addAll(modelMapper.map(sgSet, 
				new TypeToken<Set<OpenOptimizationSetDTO>>() {}.getType()));

		return osSetDTO;
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
	public Set<MetricDTO> getMetrics(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Metric> metrics = item.getMetrics(); 
		return modelMapper.map(metrics, new TypeToken<Set<MetricDTO>>() {}.getType());
	}

	@Override
	@Transactional(readOnly = true)
	public int getSimulationmodelId(int prjid) {
		Project p = projectRepository.findOne(prjid);
		
		if(p == null)
			return 0;
		
		return p.getSimulationmodel() != null 
				? p.getSimulationmodel().getModelid() 
				: null;
	}

	@Override
	@Transactional(readOnly = true)
	public int getDefaultExtParamSetId(int prjid){
		Project p = projectRepository.findOne(prjid);
		
		if(p == null)
			return 0;
		
		return p.getDefaultextparamvalset() != null 
				? p.getDefaultextparamvalset().getExtparamvalsetid()
				: 0;
	}
}


