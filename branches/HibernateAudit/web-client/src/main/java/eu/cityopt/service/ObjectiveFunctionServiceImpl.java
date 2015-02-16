package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.repository.ObjectiveFunctionRepository;

@Service("ObjectiveFunctionService")
public class ObjectiveFunctionServiceImpl implements ObjectiveFunctionService {
	
	@Autowired
	private ObjectiveFunctionRepository objectiveFunctionRepository;
	
	public List<ObjectiveFunction> findAll() {
		return objectiveFunctionRepository.findAll();
	}

	@Transactional
	public ObjectiveFunction save(ObjectiveFunction u) {
		return objectiveFunctionRepository.save(u);
	}

	@Transactional
	public void delete(ObjectiveFunction u) throws EntityNotFoundException {
		
		if(objectiveFunctionRepository.findOne(u.getObtfunctionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		objectiveFunctionRepository.delete(u);
	}
	
	@Transactional
	public ObjectiveFunction update(ObjectiveFunction toUpdate) throws EntityNotFoundException {
		
		if(objectiveFunctionRepository.findOne(toUpdate.getObtfunctionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ObjectiveFunction findByID(Integer id) {
		return objectiveFunctionRepository.findOne(id);
	}
	
}
