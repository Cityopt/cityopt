package com.cityopt.service;

import java.util.List;

import com.cityopt.model.OptSearchConst;

public interface OptSearchConstService {

	List<OptSearchConst> findAll();

	OptSearchConst save(OptSearchConst u);

	void delete(OptSearchConst u);

	OptSearchConst findByID(Integer id);

}