package com.cityopt.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cityopt.model.SimulationModel;
import com.cityopt.repository.SimulationModelRepository;

	@Service("SimulationModelService")
	public class SimulationModelServiceImpl implements SimulationModelService{

	@Autowired
	private SimulationModelRepository simulationModelRepository;
	
	public List<SimulationModel> findAll() {
		return simulationModelRepository.findAll();
	}
	
	public SimulationModel findByID(Integer id) {
		return simulationModelRepository.findOne(id);
	}
	
	@Transactional
	public SimulationModel save(SimulationModel model) {
		return simulationModelRepository.save(model);
	}
	
	@Transactional
	public void delete(SimulationModel u) throws EntityNotFoundException {
		
		if(simulationModelRepository.findOne(u.getModelid()) == null) {
			throw new EntityNotFoundException();
		}
		
		simulationModelRepository.delete(u);
	}
	
	@Transactional
	public SimulationModel update(SimulationModel toUpdate) throws EntityNotFoundException {
		
		if(simulationModelRepository.findOne(toUpdate.getModelid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional
	public void deleteAll() {
		simulationModelRepository.deleteAll();
	}

}
