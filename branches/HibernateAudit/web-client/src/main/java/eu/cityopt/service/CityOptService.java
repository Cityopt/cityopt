package eu.cityopt.service;

import java.util.List;

public interface CityOptService<T> {
	List<T> findAll();

	T save(T u);

	void delete(T toDelete) throws EntityNotFoundException;

	T update(T toUpdate) throws EntityNotFoundException;
	
	T findByID(Integer id);
}
