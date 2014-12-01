package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.TimeSeries;
import com.cityopt.repository.TimeSeriesRepository;

@Service("TimeSeriesService")
public class TimeSeriesServiceImpl implements TimeSeriesService {
	
	@Autowired
	private TimeSeriesRepository timeSeriesRepository;
	
	public List<TimeSeries> findAll() {
		return timeSeriesRepository.findAll();
	}

	@Transactional
	public TimeSeries save(TimeSeries u) {
		return timeSeriesRepository.save(u);
	}

	@Transactional
	public void delete(TimeSeries u) {
		timeSeriesRepository.delete(u);
	}
	
	public TimeSeries findByID(Integer id) {
		return timeSeriesRepository.findOne(id);
	}
	
}
