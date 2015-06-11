package eu.cityopt.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioMetricsDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.repository.InputParamValRepository;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ScenarioService;

@Service("ScenarioService")
public class ScenarioServiceImpl implements ScenarioService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ScenarioRepository scenarioRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired 
	private MetricValRepository metricValRepository;
	
	@Autowired
	InputParamValRepository inputParamValRepository;
	
	@PersistenceContext
    private EntityManager em;
	
	@Override
	@Transactional(readOnly=true)
	public List<ScenarioDTO> findAll(){
		return modelMapper.map(scenarioRepository.findAll(), 
				new TypeToken<List<ScenarioDTO>>() {}.getType());
	}

	@Override
	@Transactional
	public ScenarioDTO save(ScenarioDTO s, int prjid){
		Scenario scen = modelMapper.map(s, Scenario.class);
		Project p = projectRepository.findOne(prjid);
		scen.setProject(p);
		scen = scenarioRepository.save(scen);
		ScenarioDTO scenRet = modelMapper.map(scen, ScenarioDTO.class);
		return scenRet;
	}
	
	@Override
	@Transactional
	public ScenarioDTO saveWithDefaultInputValues(ScenarioDTO s, int prjid) throws EntityNotFoundException{
		Scenario scen = modelMapper.map(s, Scenario.class);
		Project p = projectRepository.findOne(prjid);
		
		if(p == null)
			throw new EntityNotFoundException();
		
		scen.setProject(p);
		scen = scenarioRepository.save(scen);
		
		// Create input param vals for all input params
		for (Component c : p.getComponents()){

			for(InputParameter i : c.getInputparameters()){
				InputParamVal inputParamVal = new InputParamVal();
				inputParamVal.setInputparameter(i);
				inputParamVal.setValue(i.getDefaultvalue());
				inputParamVal.setScenario(scen);
				inputParamVal = inputParamValRepository.save(inputParamVal);
			}
		}
					
		ScenarioDTO scenRet = modelMapper.map(scen, ScenarioDTO.class);
		return scenRet;
	}
	
	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(scenarioRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		scenarioRepository.delete(id);
	}
	
	@Override
	@Transactional
	public ScenarioDTO update(ScenarioDTO toUpdate, int prjid) throws EntityNotFoundException {
		
		if(scenarioRepository.findOne(toUpdate.getScenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate, prjid);
	}

	@Override
	@Transactional(readOnly=true)
	public ScenarioDTO findByID(int id) throws EntityNotFoundException {
		Scenario scen = scenarioRepository.findOne(id);
		if(scen == null) 
			throw new EntityNotFoundException();
		
		return modelMapper.map(scen, ScenarioDTO.class);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Set<InputParamValDTO> getInputParamVals(int scenId)	{
		Scenario scen = scenarioRepository.findOne(scenId);
		Set<InputParamVal> inputParamVals = scen.getInputparamvals();
		return modelMapper.map(inputParamVals, new TypeToken<Set<InputParamValDTO>>() {}.getType());
	}

	@Override
	@Transactional(readOnly=true)
	public Set<ScenarioMetricsDTO> getScenarioMetrics(int scenId)	{
		Scenario scen = scenarioRepository.findOne(scenId);
		Set<ScenarioMetrics> scenarioMetrics = scen.getScenariometricses();
		return modelMapper.map(scenarioMetrics, new TypeToken<Set<ScenarioMetricsDTO>>() {}.getType());
	}
	
	@Override
	@Transactional(readOnly=true)
	public Set<MetricValDTO> getMetricsValues(int scenId)	{
		List<MetricVal> mvList = metricValRepository.findByScenId(scenId);
		return modelMapper.map(mvList, 
				new TypeToken<Set<MetricValDTO>>() {}.getType());
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ScenarioDTO> findByName(String name) {
		List<Scenario> scenarios = scenarioRepository.findByNameContaining(name);
		List<ScenarioDTO> result 
			= modelMapper.map(scenarios, new TypeToken<List<ScenarioDTO>>() {}.getType());
		return result;
	}
	
	@Override
	@Transactional(readOnly=true)
	public Set<SimulationResultDTO> getSimulationResults(int scenId) {
		Scenario scen = scenarioRepository.findOne(scenId);
		Set<SimulationResult> simRes = scen.getSimulationresults();
		return modelMapper.map(simRes, new TypeToken<Set<SimulationResultDTO>>() {}.getType());
	}
	
//	public List<ScenarioDTO> findByCreationDate(Date dateLower, Date dateUpper){
//		return scenarioRepository.findByCreationDate(dateLower, dateUpper);
//	}
}
