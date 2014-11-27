package com.cityopt.service;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.Scenario;

public interface ScenarioService {

	List<Scenario> findAll();

	Scenario save(Scenario s);

	void delete(Scenario s);

	Scenario findByID(Integer id);

	List<Scenario> findByCreationDate(Date dateLower, Date dateUpper);

}