package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.SearchConstraint;
import com.cityopt.repository.SearchConstraintRepository;

@Service("SearchConstraintService")
public class SearchConstraintServiceImpl implements SearchConstrintService {
	
	@Autowired
	private SearchConstraintRepository searchConstraintRepository;
	
	public List<SearchConstraint> findAll() {
		return searchConstraintRepository.findAll();
	}

	@Transactional
	public SearchConstraint save(SearchConstraint u) {
		return searchConstraintRepository.save(u);
	}

	@Transactional
	public void delete(SearchConstraint u) {
		searchConstraintRepository.delete(u);
	}
	
	public SearchConstraint findByID(Integer id) {
		return searchConstraintRepository.findOne(id);
	}
	
}
