package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.SimulationResult;
import eu.cityopt.repository.SimulationResultRepository;

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
	public void delete(Integer id) throws EntityNotFoundException {
		
		if(simulationResultRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		simulationResultRepository.delete(id);
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
