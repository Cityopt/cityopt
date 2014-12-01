package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.OptimizationSet;
import com.cityopt.repository.OptimizationSetRepository;

@Service("OptimizationSetService")
public class OptimizationSetServiceImpl implements OptimizationSetService {
	
	@Autowired
	private OptimizationSetRepository optimizationSetRepository;
	
	public List<OptimizationSet> findAll() {
		return optimizationSetRepository.findAll();
	}

	@Transactional
	public OptimizationSet save(OptimizationSet u) {
		return optimizationSetRepository.save(u);
	}

	@Transactional
	public void delete(OptimizationSet u) {
		optimizationSetRepository.delete(u);
	}
	
	public OptimizationSet findByID(Integer id) {
		return optimizationSetRepository.findOne(id);
	}
	
}
