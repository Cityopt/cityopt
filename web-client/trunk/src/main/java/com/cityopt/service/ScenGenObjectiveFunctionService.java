package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ScenGenObjectiveFunction;

public interface ScenGenObjectiveFunctionService {

	List<ScenGenObjectiveFunction> findAll();

	ScenGenObjectiveFunction save(ScenGenObjectiveFunction u);

	void delete(ScenGenObjectiveFunction u);

	ScenGenObjectiveFunction findByID(Integer id);

}