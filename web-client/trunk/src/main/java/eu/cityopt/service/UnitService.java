package eu.cityopt.service;

import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.model.Unit;

public interface UnitService extends CityOptService<UnitDTO> {
	
	public UnitDTO save(UnitDTO u);
	
	public UnitDTO update(UnitDTO toUpdate) throws EntityNotFoundException;

	public UnitDTO findByID(int id) throws EntityNotFoundException;
}