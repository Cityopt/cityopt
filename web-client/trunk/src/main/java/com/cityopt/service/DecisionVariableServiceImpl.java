package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.DecisionVariable;
import com.cityopt.repository.DecisionVariableRepository;

@Service("DecisionVariableService")
public class DecisionVariableServiceImpl implements DecisionVariableService {
	
	@Autowired
	private DecisionVariableRepository decisionVariableRepository;
	
	public List<DecisionVariable> findAll() {
		return decisionVariableRepository.findAll();
	}

	@Transactional
	public DecisionVariable save(DecisionVariable u) {
		return decisionVariableRepository.save(u);
	}

	@Transactional
	public void delete(DecisionVariable u) {
		decisionVariableRepository.delete(u);
	}
	
	public DecisionVariable findByID(Integer id) {
		return decisionVariableRepository.findOne(id);
	}
	
}
