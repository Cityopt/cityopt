package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ScenGenObjectiveFunction;
import com.cityopt.repository.ScenGenObjectiveFunctionRepository;

@Service("ScenGenObjectiveFunctionService")
public class ScenGenObjectiveFunctionServiceImpl implements ScenGenObjectiveFunctionService {
	
	@Autowired
	private ScenGenObjectiveFunctionRepository scenGenObjectiveFunctionRepository;
	
	public List<ScenGenObjectiveFunction> findAll() {
		return scenGenObjectiveFunctionRepository.findAll();
	}

	@Transactional
	public ScenGenObjectiveFunction save(ScenGenObjectiveFunction u) {
		return scenGenObjectiveFunctionRepository.save(u);
	}

	@Transactional
	public void delete(ScenGenObjectiveFunction u) throws EntityNotFoundException {
		
		if(scenGenObjectiveFunctionRepository.findOne(u.getSgobfunctionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		scenGenObjectiveFunctionRepository.delete(u);
	}
	
	@Transactional
	public ScenGenObjectiveFunction update(ScenGenObjectiveFunction toUpdate) throws EntityNotFoundException {
		
		if(scenGenObjectiveFunctionRepository.findOne(toUpdate.getSgobfunctionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ScenGenObjectiveFunction findByID(Integer id) {
		return scenGenObjectiveFunctionRepository.findOne(id);
	}
	
}
