package com.cityopt.service;

import java.util.List;

import com.cityopt.model.Type;

public interface TypeService {

	List<Type> findAll();

	Type save(Type u);

	void delete(Type u);

	Type findByID(Integer id);

}