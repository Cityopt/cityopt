package com.cityopt.service;

import java.util.List;



import com.cityopt.model.Simulationmodel;

public interface SimulationModelService {
	List<Simulationmodel> findAllSimulationModel();

	Simulationmodel save(Simulationmodel model);
	
	void deleteAll();

}
