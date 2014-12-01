package com.cityopt.service;

import java.util.List;

import com.cityopt.model.OptimizationSet;

public interface OptimizationSetService {

	List<OptimizationSet> findAll();

	OptimizationSet save(OptimizationSet u);

	void delete(OptimizationSet u);

	OptimizationSet findByID(Integer id);

}