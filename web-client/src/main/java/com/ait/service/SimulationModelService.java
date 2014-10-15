package com.ait.service;

import java.util.List;


import com.ait.model.Simulationmodel;

public interface SimulationModelService {
	List<Simulationmodel> findAllSimulationModel();

	Simulationmodel save(Simulationmodel model);
	
	void deleteAll();

}
