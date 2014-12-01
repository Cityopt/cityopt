package com.cityopt.service;

import java.util.List;

import com.cityopt.model.OptConstraint;

public interface OptConstraintService {

	List<OptConstraint> findAll();

	OptConstraint save(OptConstraint u);

	void delete(OptConstraint u);

	OptConstraint findByID(Integer id);

}