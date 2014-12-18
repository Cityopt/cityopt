package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.OptSetScenarios;
import eu.cityopt.repository.OptSetScenariosRepository;

public class OptSetScenariosImpl implements OptSetScenariosService {
	
	@Autowired
	private OptSetScenariosRepository optSetScenariosRepository;
	
	public List<OptSetScenarios> findAll() {
		return optSetScenariosRepository.findAll();
	}

	@Transactional
	public OptSetScenarios save(OptSetScenarios u) {
		return optSetScenariosRepository.save(u);
	}

	@Transactional
	public void delete(OptSetScenarios u) throws EntityNotFoundException {
		
		if(optSetScenariosRepository.findOne(u.getOptscenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		optSetScenariosRepository.delete(u);
	}
	
	@Transactional
	public OptSetScenarios update(OptSetScenarios toUpdate) throws EntityNotFoundException {
		
		if(optSetScenariosRepository.findOne(toUpdate.getOptscenid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public OptSetScenarios findByID(Integer id) {
		return optSetScenariosRepository.findOne(id);
	}
}