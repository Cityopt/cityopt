package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.OptConstraint;
import com.cityopt.repository.OptConstraintRepository;

@Service("OptConstraintService")
public class OptConstraintServiceImpl implements OptConstraintService {
	
	@Autowired
	private OptConstraintRepository optConstraintRepository;
	
	public List<OptConstraint> findAll() {
		return optConstraintRepository.findAll();
	}

	@Transactional
	public OptConstraint save(OptConstraint u) {
		return optConstraintRepository.save(u);
	}

	@Transactional
	public void delete(OptConstraint u) {
		optConstraintRepository.delete(u);
	}
	
	public OptConstraint findByID(Integer id) {
		return optConstraintRepository.findOne(id);
	}
	
}
