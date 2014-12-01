package com.cityopt.service;

import java.util.List;

import com.cityopt.model.AlgoParamVal;

public interface AlgoParamValService {

	List<AlgoParamVal> findAll();

	AlgoParamVal save(AlgoParamVal u);

	void delete(AlgoParamVal u);

	AlgoParamVal findByID(Integer id);

}