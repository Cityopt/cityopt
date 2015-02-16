package eu.cityopt.service;

import java.util.List;

import eu.cityopt.model.ExtParam;

public interface ExtParamService {

	List<ExtParam> findAll();

	ExtParam save(ExtParam u);

	void delete(ExtParam u) throws EntityNotFoundException;
	
	ExtParam update(ExtParam toUpdate) throws EntityNotFoundException;

	ExtParam findByID(Integer id);

}