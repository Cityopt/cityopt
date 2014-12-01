package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ScenarioGenerator;

public interface ScenarioGeneratorService {

	List<ScenarioGenerator> findAll();

	ScenarioGenerator save(ScenarioGenerator u);

	void delete(ScenarioGenerator u);

	ScenarioGenerator findByID(Integer id);

}