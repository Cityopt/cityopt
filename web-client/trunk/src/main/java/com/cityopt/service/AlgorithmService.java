package com.cityopt.service;

import java.util.List;

import com.cityopt.model.Algorithm;

public interface AlgorithmService {

	List<Algorithm> findAll();

	Algorithm save(Algorithm u);

	void delete(Algorithm u);

	Algorithm findByID(Integer id);

}