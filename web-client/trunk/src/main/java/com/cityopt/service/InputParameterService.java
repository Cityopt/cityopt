package com.cityopt.service;

import java.util.List;

import com.cityopt.model.InputParameter;

public interface InputParameterService {

	List<InputParameter> findAll();

	InputParameter save(InputParameter u);

	void delete(InputParameter u);

	InputParameter findByID(Integer id);

}