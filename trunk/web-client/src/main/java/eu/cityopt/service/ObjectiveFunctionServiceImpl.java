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
	
	@Transactional(readOnly=true)
	public List<ObjectiveFunction> findAll() {
		return objectiveFunctionRepository.findAll();
	}

	@Transactional
	public ObjectiveFunction save(ObjectiveFunction u) {
		return objectiveFunctionRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(objectiveFunctionRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		objectiveFunctionRepository.delete(id);
	}
	
	@Transactional
	public ObjectiveFunction update(ObjectiveFunction toUpdate) throws EntityNotFoundException {
		
		if(objectiveFunctionRepository.findOne(toUpdate.getObtfunctionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public ObjectiveFunction findByID(int id) {
		return objectiveFunctionRepository.findOne(id);
	}
	
}
