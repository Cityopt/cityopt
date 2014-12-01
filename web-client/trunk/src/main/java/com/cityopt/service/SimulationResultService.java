package com.cityopt.service;

import java.util.List;

import com.cityopt.model.SimulationResult;

public interface SimulationResultService {

	List<SimulationResult> findAll();

	SimulationResult save(SimulationResult u);

	void delete(SimulationResult u);

	SimulationResult findByID(Integer id);

}