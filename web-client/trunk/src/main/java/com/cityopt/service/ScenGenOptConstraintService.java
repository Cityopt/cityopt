package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ScenGenOptConstraint;

public interface ScenGenOptConstraintService {

	List<ScenGenOptConstraint> findAll();

	ScenGenOptConstraint save(ScenGenOptConstraint u);

	void delete(ScenGenOptConstraint u);

	ScenGenOptConstraint findByID(Integer id);

}