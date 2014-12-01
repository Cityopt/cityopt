package com.cityopt.service;

import java.util.List;

import com.cityopt.model.Metric;

public interface MetricService {

	List<Metric> findAll();

	Metric save(Metric u);

	void delete(Metric u);

	Metric findByID(Integer id);

}