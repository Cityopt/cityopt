package eu.cityopt.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.SimulationModelDTO;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationModel;
import eu.cityopt.repository.SimulationModelRepository;

@Service("SimulationModelService")
public class SimulationModelServiceImpl implements SimulationModelService {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private SimulationModelRepository simulationModelRepository;
	
	@Transactional(readOnly=true)
	public List<SimulationModelDTO> findAll() {
		
		return modelMapper.map(simulationModelRepository.findAll(), 
			new TypeToken<List<SimulationModelDTO>>() {}.getType());
	}
	
	@Transactional(readOnly=true)
	public SimulationModelDTO findByID(int id) throws EntityNotFoundException {
		SimulationModel sim = simulationModelRepository.findOne(id);
		if(sim == null) 
			throw new EntityNotFoundException();
		
		return modelMapper.map(sim, SimulationModelDTO.class);
	}
	
	@Transactional
	public SimulationModelDTO save(SimulationModel model) {
		
		SimulationModel sim = modelMapper.map(model, SimulationModel.class);
		sim = simulationModelRepository.save(sim);
		SimulationModelDTO simRet = modelMapper.map(sim, SimulationModelDTO.class);
		return simRet;
		
	}
	
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(simulationModelRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		simulationModelRepository.delete(id);
	}
	
	@Transactional
	public SimulationModelDTO update(SimulationModel toUpdate) throws EntityNotFoundException {
		
		if(simulationModelRepository.findOne(toUpdate.getModelid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
//	@Transactional
//	public void deleteAll() {
//		simulationModelRepository.deleteAll();
//	}

}
