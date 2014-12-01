package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ScenarioMetrics;
import com.cityopt.repository.ScenarioMetricsRepository;

@Service("ScenarioMetricsService")
public class ScenarioMetricsServiceImpl implements ScenarioMetricsService {
	
	@Autowired
	private ScenarioMetricsRepository scenarioMetricsRepository;
	
	public List<ScenarioMetrics> findAll() {
		return scenarioMetricsRepository.findAll();
	}

	@Transactional
	public ScenarioMetrics save(ScenarioMetrics u) {
		return scenarioMetricsRepository.save(u);
	}

	@Transactional
	public void delete(ScenarioMetrics u) {
		scenarioMetricsRepository.delete(u);
	}
	
	public ScenarioMetrics findByID(Integer id) {
		return scenarioMetricsRepository.findOne(id);
	}
	
}
