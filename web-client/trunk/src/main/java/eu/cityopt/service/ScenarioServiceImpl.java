package eu.cityopt.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;

@Service("ScenarioService")
public class ScenarioServiceImpl {
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

	public ScenarioDTO findByID(Integer id){
		Scenario scen = scenarioRepository.findOne(id);
		return modelMapper.map(scen, ScenarioDTO.class);
	}

//	public List<ScenarioDTO> findByCreationDate(Date dateLower, Date dateUpper){
//		return scenarioRepository.findByCreationDate(dateLower, dateUpper);
//	}
}
