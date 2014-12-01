package com.cityopt.service;

import java.util.List;

import com.cityopt.model.AlgoParam;

public interface AlgoParamService {

	List<AlgoParam> findAll();

	AlgoParam save(AlgoParam u);

	void delete(AlgoParam u);

	AlgoParam findByID(Integer id);

}