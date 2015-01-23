package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioMetricsDTO;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;

@Service("ScenarioService")
public class ScenarioServiceImpl implements ScenarioService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ScenarioRepository scenarioRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@PersistenceContext
    private EntityManager em;
	
	public List<ScenarioDTO> findAll(){
		return modelMapper.map(scenarioRepository.findAll(), 
				new TypeToken<List<ScenarioDTO>>() {}.getType());
	}

	@Transactional
	public ScenarioDTO save(ScenarioDTO s, int prjid){
		Scenario scen = modelMapper.map(s, Scenario.class);
		Project p = projectRepository.findOne(prjid);
		scen.setProject(p);
		scen = scenarioRepository.save(scen);
		ScenarioDTO scenRet = modelMapper.map(scen, ScenarioDTO.class);
		return scenRet;
	}
	
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(scenarioRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		scenarioRepository.delete(id);
	}
	
	@Transactional
	public ScenarioDTO update(ScenarioDTO toUpdate, int prjid) throws EntityNotFoundException {
		
		if(scenarioRepository.findOne(toUpdate.getScenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate, prjid);
	}

	public ScenarioDTO findByID(int id){
		Scenario scen = scenarioRepository.findOne(id);
		return modelMapper.map(scen, ScenarioDTO.class);
	}
	
	public Set<InputParamValDTO> getInputParamVals(int scenId)	{
		Scenario scen = scenarioRepository.findOne(scenId);
		Set<InputParamVal> inputParamVals = scen.getInputparamvals();
		return modelMapper.map(inputParamVals, new TypeToken<Set<InputParamValDTO>>() {}.getType());
	}

	public Set<ScenarioMetricsDTO> getScenarioMetrics(int scenId)	{
		Scenario scen = scenarioRepository.findOne(scenId);
		Set<ScenarioMetrics> scenarioMetrics = scen.getScenariometricses();
		return modelMapper.map(scenarioMetrics, new TypeToken<Set<ScenarioMetricsDTO>>() {}.getType());
	}
	
	public List<ScenarioDTO> findByName(String name) {
		List<Scenario> scenarios = scenarioRepository.findByName(name);
		List<ScenarioDTO> result 
			= modelMapper.map(scenarios, new TypeToken<List<ScenarioDTO>>() {}.getType());
		return result;
	}
	
//	public List<ScenarioDTO> findByCreationDate(Date dateLower, Date dateUpper){
//		return scenarioRepository.findByCreationDate(dateLower, dateUpper);
//	}
}
