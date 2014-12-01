package com.cityopt.service;

import java.util.List;

import com.cityopt.model.ModelParameter;

public interface ModelParameterService {

	List<ModelParameter> findAll();

	ModelParameter save(ModelParameter u);

	void delete(ModelParameter u);

	ModelParameter findByID(Integer id);

}