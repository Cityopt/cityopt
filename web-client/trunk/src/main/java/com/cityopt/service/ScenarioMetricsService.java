package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ScenarioMetrics;

public interface ScenarioMetricsService {

	List<ScenarioMetrics> findAll();

	ScenarioMetrics save(ScenarioMetrics u);

	void delete(ScenarioMetrics u);

	ScenarioMetrics findByID(Integer id);

}