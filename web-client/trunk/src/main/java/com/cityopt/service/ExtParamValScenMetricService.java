package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ExtParamValScenMetric;

public interface ExtParamValScenMetricService {

	List<ExtParamValScenMetric> findAll();

	ExtParamValScenMetric save(ExtParamValScenMetric u);

	void delete(ExtParamValScenMetric u);

	ExtParamValScenMetric findByID(Integer id);

}