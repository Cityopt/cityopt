package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.Metric;
import com.cityopt.repository.MetricRepository;

@Service("MetricService")
public class MetricServiceImpl implements MetricService {
	
	@Autowired
	private MetricRepository metricRepository;
	
	public List<Metric> findAll() {
		return metricRepository.findAll();
	}

	@Transactional
	public Metric save(Metric u) {
		return metricRepository.save(u);
	}

	@Transactional
	public void delete(Metric u) {
		metricRepository.delete(u);
	}
	
	public Metric findByID(Integer id) {
		return metricRepository.findOne(id);
	}
	
}
