package com.cityopt.service;

import java.util.List;

import com.cityopt.model.Component;

public interface ComponentService {

	List<Component> findAll();

	Component save(Component u);

	void delete(Component u);

	Component findByID(Integer id);

}