package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.InputParamVal;
import com.cityopt.repository.InputParamValRepository;

@Service("InputParamValService")
public class InputParamValServiceImpl implements InputParamValService {
	
	@Autowired
	private InputParamValRepository inputParamValRepository;
	
	public List<InputParamVal> findAll() {
		return inputParamValRepository.findAll();
	}

	@Transactional
	public InputParamVal save(InputParamVal u) {
		return inputParamValRepository.save(u);
	}

	@Transactional
	public void delete(InputParamVal u) {
		inputParamValRepository.delete(u);
	}
	
	public InputParamVal findByID(Integer id) {
		return inputParamValRepository.findOne(id);
	}
	
}
