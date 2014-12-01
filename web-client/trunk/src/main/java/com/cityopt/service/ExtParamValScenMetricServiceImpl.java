package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ExtParamValScenMetric;
import com.cityopt.repository.ExtParamValScenMetricRepository;

@Service("ExtParamValScenMetricService")
public class ExtParamValScenMetricServiceImpl implements ExtParamValScenMetricService {
	
	@Autowired
	private ExtParamValScenMetricRepository extParamValScenMetricRepository;
	
	public List<ExtParamValScenMetric> findAll() {
		return extParamValScenMetricRepository.findAll();
	}

	@Transactional
	public ExtParamValScenMetric save(ExtParamValScenMetric u) {
		return extParamValScenMetricRepository.save(u);
	}

	@Transactional
	public void delete(ExtParamValScenMetric u) {
		extParamValScenMetricRepository.delete(u);
	}
	
	public ExtParamValScenMetric findByID(Integer id) {
		return extParamValScenMetricRepository.findOne(id);
	}
	
}
