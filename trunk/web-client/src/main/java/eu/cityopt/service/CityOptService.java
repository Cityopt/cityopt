package eu.cityopt.service;

import java.util.List;

public interface CityOptService<T> {
	List<T> findAll();

//	T save(T u);

	void delete(int id) throws EntityNotFoundException;

//	T update(T toUpdate) throws EntityNotFoundException;
	
	T findByID(int id) throws EntityNotFoundException;
}
