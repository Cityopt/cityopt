package com.cityopt.service;

import java.util.List;

import com.cityopt.model.InputParamVal;

public interface InputParamValService {

	List<InputParamVal> findAll();

	InputParamVal save(InputParamVal u);

	void delete(InputParamVal u);

	InputParamVal findByID(Integer id);

}