package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.ScenarioGeneratorRepository;

@Service("ScenarioGeneratorService")
public class ScenarioGeneratorServiceImpl implements ScenarioGeneratorService {
	
	@Autowired
	private ScenarioGeneratorRepository scenarioGeneratorRepository;
	
	public List<ScenarioGenerator> findAll() {
		return scenarioGeneratorRepository.findAll();
	}

	@Transactional
	public ScenarioGenerator save(ScenarioGenerator u) {
		return scenarioGeneratorRepository.save(u);
	}

	@Transactional
	public void delete(ScenarioGenerator u) throws EntityNotFoundException {
		
		if(scenarioGeneratorRepository.findOne(u.getScengenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		scenarioGeneratorRepository.delete(u);
	}
	
	@Transactional
	public ScenarioGenerator update(ScenarioGenerator toUpdate) throws EntityNotFoundException {
		
		if(scenarioGeneratorRepository.findOne(toUpdate.getScengenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ScenarioGenerator findByID(Integer id) {
		return scenarioGeneratorRepository.findOne(id);
	}
	
}
