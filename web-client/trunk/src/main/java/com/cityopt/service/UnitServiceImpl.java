package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.Unit;
import com.cityopt.repository.UnitRepository;

@Service("UnitService")
public class UnitServiceImpl implements UnitService {
	
	@Autowired
	private UnitRepository unitRepository;
	
	public List<Unit> findAll() {
		return unitRepository.findAll();
	}

	@Transactional
	public Unit save(Unit u) {
		return unitRepository.save(u);
	}

	@Transactional
	public void delete(Unit u) {
		unitRepository.delete(u);
	}
	
	public Unit findByID(Integer id) {
		return unitRepository.findOne(id);
	}
	
}
