package com.cityopt.service;

import java.util.List;

import com.cityopt.model.SimulationModel;

public interface SimulationModelService {
	List<SimulationModel> findAllSimulationModel();

	SimulationModel save(SimulationModel model);
	
	void deleteAll();

}
