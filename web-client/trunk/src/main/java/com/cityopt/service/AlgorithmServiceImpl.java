package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.Algorithm;
import com.cityopt.repository.AlgorithmRepository;

@Service("AlgorithmService")
public class AlgorithmServiceImpl implements AlgorithmService{
	
	@Autowired
	private AlgorithmRepository algorithmRepository;
	
	public List<Algorithm> findAll() {
		return algorithmRepository.findAll();
	}

	@Transactional
	public Algorithm save(Algorithm u) {
		return algorithmRepository.save(u);
	}

	@Transactional
	public void delete(Algorithm u) {
		algorithmRepository.delete(u);
	}
	
	public Algorithm findByID(Integer id) {
		return algorithmRepository.findOne(id);
	}
	
}
