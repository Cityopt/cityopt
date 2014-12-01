package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.OutputVariable;
import com.cityopt.repository.OutputVariableRepository;

@Service("OutputVariableService")
public class OutputVariableServiceImpl implements OutputVariableService {
	
	@Autowired
	private OutputVariableRepository outputVariableRepository;
	
	public List<OutputVariable> findAll() {
		return outputVariableRepository.findAll();
	}

	@Transactional
	public OutputVariable save(OutputVariable u) {
		return outputVariableRepository.save(u);
	}

	@Transactional
	public void delete(OutputVariable u) {
		outputVariableRepository.delete(u);
	}
	
	public OutputVariable findByID(Integer id) {
		return outputVariableRepository.findOne(id);
	}
	
}
