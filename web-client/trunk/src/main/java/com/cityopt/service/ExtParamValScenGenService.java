package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ExtParamValScenGen;

public interface ExtParamValScenGenService {

	List<ExtParamValScenGen> findAll();

	ExtParamValScenGen save(ExtParamValScenGen u);

	void delete(ExtParamValScenGen u) throws EntityNotFoundException;
	
	ExtParamValScenGen update(ExtParamValScenGen toUpdate) throws EntityNotFoundException;

	ExtParamValScenGen findByID(Integer id);

}