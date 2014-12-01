package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.DataReliability;
import com.cityopt.repository.DataReliabilityRepository;

@Service("DataReliabilityService")
public class DataReliabilityServiceImpl implements DataReliabilityService {
	
	@Autowired
	private DataReliabilityRepository dataReliabilityRepository;
	
	public List<DataReliability> findAll() {
		return dataReliabilityRepository.findAll();
	}

	@Transactional
	public DataReliability save(DataReliability u) {
		return dataReliabilityRepository.save(u);
	}

	@Transactional
	public void delete(DataReliability u) {
		dataReliabilityRepository.delete(u);
	}
	
	public DataReliability findByID(Integer id) {
		return dataReliabilityRepository.findOne(id);
	}
	
}
