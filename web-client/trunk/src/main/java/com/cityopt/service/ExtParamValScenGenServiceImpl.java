package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ExtParamValScenGen;
import com.cityopt.repository.ExtParamValScenGenRepository;

@Service("ExtParamValScenGenService")
public class ExtParamValScenGenServiceImpl implements ExtParamValScenGenService {
	
	@Autowired
	private ExtParamValScenGenRepository extParamValScenGenRepository;
	
	public List<ExtParamValScenGen> findAll() {
		return extParamValScenGenRepository.findAll();
	}

	@Transactional
	public ExtParamValScenGen save(ExtParamValScenGen u) {
		return extParamValScenGenRepository.save(u);
	}

	@Transactional
	public void delete(ExtParamValScenGen u) {
		extParamValScenGenRepository.delete(u);
	}
	
	public ExtParamValScenGen findByID(Integer id) {
		return extParamValScenGenRepository.findOne(id);
	}
	
}
