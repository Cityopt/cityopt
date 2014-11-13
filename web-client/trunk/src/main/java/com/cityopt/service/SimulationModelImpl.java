package com.cityopt.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cityopt.model.Simulationmodel;
import com.cityopt.repository.SimulationModelRepository;

	@Service("SimulationModelService")
	public class SimulationModelImpl implements SimulationModelService{

	@Autowired
	private SimulationModelRepository simulationModelRepository;
	
	public List<Simulationmodel> findAllSimulationModel() {
		return simulationModelRepository.findAll();
	}
	
	@Transactional
	public Simulationmodel save(Simulationmodel model) {
		return simulationModelRepository.save(model);
	}
	
	@Transactional
	public void deleteAll() {
		simulationModelRepository.deleteAll();
	}

}
