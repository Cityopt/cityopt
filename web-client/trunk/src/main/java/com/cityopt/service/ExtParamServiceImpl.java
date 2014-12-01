package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ExtParam;
import com.cityopt.repository.ExtParamRepository;

@Service("ExtParamService")
public class ExtParamServiceImpl implements ExtParamService {
	
	@Autowired
	private ExtParamRepository extParamRepository;
	
	public List<ExtParam> findAll() {
		return extParamRepository.findAll();
	}

	@Transactional
	public ExtParam save(ExtParam u) {
		return extParamRepository.save(u);
	}

	@Transactional
	public void delete(ExtParam u) {
		extParamRepository.delete(u);
	}
	
	public ExtParam findByID(Integer id) {
		return extParamRepository.findOne(id);
	}
	
}
