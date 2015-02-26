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
	
	@Transactional(readOnly=true)
	public List<ScenarioGenerator> findAll() {
		return scenarioGeneratorRepository.findAll();
	}

	@Transactional
	public ScenarioGenerator save(ScenarioGenerator u) {
		return scenarioGeneratorRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(scenarioGeneratorRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		scenarioGeneratorRepository.delete(id);
	}
	
	@Transactional
	public ScenarioGenerator update(ScenarioGenerator toUpdate) throws EntityNotFoundException {
		
		if(scenarioGeneratorRepository.findOne(toUpdate.getScengenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public ScenarioGenerator findByID(int id) {
		return scenarioGeneratorRepository.findOne(id);
	}
	
}
