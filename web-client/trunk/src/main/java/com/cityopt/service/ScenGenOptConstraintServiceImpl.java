package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ScenGenOptConstraint;
import com.cityopt.repository.ScenGenOptConstraintRepository;

@Service("ScenGenOptConstraintService")
public class ScenGenOptConstraintServiceImpl implements ScenGenOptConstraintService {
	
	@Autowired
	private ScenGenOptConstraintRepository scenGenOptConstraintRepository;
	
	public List<ScenGenOptConstraint> findAll() {
		return scenGenOptConstraintRepository.findAll();
	}

	@Transactional
	public ScenGenOptConstraint save(ScenGenOptConstraint u) {
		return scenGenOptConstraintRepository.save(u);
	}

	@Transactional
	public void delete(ScenGenOptConstraint u) {
		scenGenOptConstraintRepository.delete(u);
	}
	
	public ScenGenOptConstraint findByID(Integer id) {
		return scenGenOptConstraintRepository.findOne(id);
	}
	
}
