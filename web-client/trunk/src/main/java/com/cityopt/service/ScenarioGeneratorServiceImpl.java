package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ScenarioGenerator;
import com.cityopt.repository.ScenarioGeneratorRepository;

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
	public void delete(ScenarioGenerator u) {
		scenarioGeneratorRepository.delete(u);
	}
	
	public ScenarioGenerator findByID(Integer id) {
		return scenarioGeneratorRepository.findOne(id);
	}
	
}
