package eu.cityopt.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.cityopt.model.SimulationModel;
import eu.cityopt.repository.SimulationModelRepository;

	@Service("SimulationModelService")
	public class SimulationModelServiceImpl implements SimulationModelService{

	@Autowired
	private SimulationModelRepository simulationModelRepository;
	
	public List<SimulationModel> findAll() {
		return simulationModelRepository.findAll();
	}
	
	public SimulationModel findByID(int id) {
		return simulationModelRepository.findOne(id);
	}
	
	@Transactional
	public SimulationModel save(SimulationModel model) {
		return simulationModelRepository.save(model);
	}
	
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(simulationModelRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		simulationModelRepository.delete(id);
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
