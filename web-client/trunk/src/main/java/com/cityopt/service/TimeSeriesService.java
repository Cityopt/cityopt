package com.cityopt.service;

import java.util.List;

import com.cityopt.model.TimeSeries;

public interface TimeSeriesService {

	List<TimeSeries> findAll();

	TimeSeries save(TimeSeries u);

	void delete(TimeSeries u);

	TimeSeries findByID(Integer id);

}