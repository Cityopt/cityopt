package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ObjectiveFunction;

public interface ObjectiveFunctionService {

	List<ObjectiveFunction> findAll();

	ObjectiveFunction save(ObjectiveFunction u);

	void delete(ObjectiveFunction u);

	ObjectiveFunction findByID(Integer id);

}