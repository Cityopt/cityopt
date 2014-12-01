package com.cityopt.service;

import java.util.List;

import com.cityopt.model.Unit;

public interface UnitService {

	List<Unit> findAll();

	Unit save(Unit u);

	void delete(Unit u);

	Unit findByID(Integer id);

}