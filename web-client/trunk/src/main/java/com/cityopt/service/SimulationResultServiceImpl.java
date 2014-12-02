package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.SimulationResult;
import com.cityopt.repository.SimulationResultRepository;

@Service("SimulationResultService")
public class SimulationResultServiceImpl implements SimulationResultService {
	
	@Autowired
	private SimulationResultRepository simulationResultRepository;
	
	public List<SimulationResult> findAll() {
		return simulationResultRepository.findAll();
	}

	@Transactional
	public SimulationResult save(SimulationResult u) {
		return simulationResultRepository.save(u);
	}

	@Transactional
	public void delete(SimulationResult u) throws EntityNotFoundException {
		
		if(simulationResultRepository.findOne(u.getScenresid()) == null) {
			throw new EntityNotFoundException();
		}
		
		simulationResultRepository.delete(u);
	}
	
	@Transactional
	public SimulationResult update(SimulationResult toUpdate) throws EntityNotFoundException {
		
		if(simulationResultRepository.findOne(toUpdate.getScenresid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public SimulationResult findByID(Integer id) {
		return simulationResultRepository.findOne(id);
	}
	
}
