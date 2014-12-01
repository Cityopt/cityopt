package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ExtParamVal;

public interface ExtParamValService {

	List<ExtParamVal> findAll();

	ExtParamVal save(ExtParamVal u);

	void delete(ExtParamVal u);

	ExtParamVal findByID(Integer id);

}