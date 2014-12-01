package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.InputParameter;
import com.cityopt.repository.InputParameterRepository;

@Service("InputParameterService")
public class InputParameterServiceImpl implements InputParameterService {
	
	@Autowired
	private InputParameterRepository inputParameterRepository;
	
	public List<InputParameter> findAll() {
		return inputParameterRepository.findAll();
	}

	@Transactional
	public InputParameter save(InputParameter u) {
		return inputParameterRepository.save(u);
	}

	@Transactional
	public void delete(InputParameter u) {
		inputParameterRepository.delete(u);
	}
	
	public InputParameter findByID(Integer id) {
		return inputParameterRepository.findOne(id);
	}
	
}
