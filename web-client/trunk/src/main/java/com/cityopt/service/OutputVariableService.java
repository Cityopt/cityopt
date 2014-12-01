package com.cityopt.service;

import java.util.List;

import com.cityopt.model.OutputVariable;

public interface OutputVariableService {

	List<OutputVariable> findAll();

	OutputVariable save(OutputVariable u);

	void delete(OutputVariable u);

	OutputVariable findByID(Integer id);

}