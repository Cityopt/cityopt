package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ObjectiveFunction;
import com.cityopt.repository.ObjectiveFunctionRepository;

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
	public void delete(ObjectiveFunction u) {
		objectiveFunctionRepository.delete(u);
	}
	
	public ObjectiveFunction findByID(Integer id) {
		return objectiveFunctionRepository.findOne(id);
	}
	
}
