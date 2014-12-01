package com.cityopt.service;

import java.util.List;

import com.cityopt.model.SearchConstraint;

public interface SearchConstrintService {

	List<SearchConstraint> findAll();

	SearchConstraint save(SearchConstraint u);

	void delete(SearchConstraint u);

	SearchConstraint findByID(Integer id);

}