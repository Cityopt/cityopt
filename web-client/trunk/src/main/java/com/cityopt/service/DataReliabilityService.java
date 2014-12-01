package com.cityopt.service;

import java.util.List;

import com.cityopt.model.DataReliability;

public interface DataReliabilityService {

	List<DataReliability> findAll();

	DataReliability save(DataReliability u);

	void delete(DataReliability u);

	DataReliability findByID(Integer id);

}