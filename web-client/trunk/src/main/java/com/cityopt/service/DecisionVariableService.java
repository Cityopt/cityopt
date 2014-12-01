package com.cityopt.service;

import java.util.List;

import com.cityopt.model.DecisionVariable;

public interface DecisionVariableService {

	List<DecisionVariable> findAll();

	DecisionVariable save(DecisionVariable u);

	void delete(DecisionVariable u);

	DecisionVariable findByID(Integer id);

}