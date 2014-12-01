package com.cityopt.service;

import java.util.List;

import com.cityopt.model.TimeSeriesVal;

public interface TimeSeriesValService {

	List<TimeSeriesVal> findAll();

	TimeSeriesVal save(TimeSeriesVal u);

	void delete(TimeSeriesVal u);

	TimeSeriesVal findByID(Integer id);

}