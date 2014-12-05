package eu.cityopt.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ScenarioRepository;

@Service("ScenarioService")
public class ScenarioServiceImpl implements ScenarioService {
	@Autowired
	private ScenarioRepository scenarioRepository;
	
	public List<Scenario> findAll(){
		return scenarioRepository.findAll();
	}

	@Transactional
	public Scenario save(Scenario s){
		return scenarioRepository.save(s);
	}
	
	@Transactional
	public void delete(Scenario s) throws EntityNotFoundException {
		
		if(scenarioRepository.findOne(s.getScenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		scenarioRepository.delete(s);
	}
	
	@Transactional
	public Scenario update(Scenario toUpdate) throws EntityNotFoundException {
		
		if(scenarioRepository.findOne(toUpdate.getScenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}

	public Scenario findByID(Integer id){
		return scenarioRepository.findOne(id);
	}

	public List<Scenario> findByCreationDate(Date dateLower, Date dateUpper){
		return scenarioRepository.findByCreationDate(dateLower, dateUpper);
	}
}
