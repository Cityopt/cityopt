package eu.cityopt.service;

import eu.cityopt.DTO.TypeDTO;

public interface TypeService extends CityOptService<TypeDTO> {
	
	public TypeDTO save(TypeDTO t);
	
	public TypeDTO update(TypeDTO toUpdate) throws EntityNotFoundException;

	public TypeDTO findByID(int id) throws EntityNotFoundException;

}