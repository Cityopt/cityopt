package eu.cityopt.service;

import eu.cityopt.DTO.DataReliabilityDTO;

public interface DataReliabilityService extends CityOptService<DataReliabilityDTO>{

	DataReliabilityDTO save(DataReliabilityDTO d);

	DataReliabilityDTO update(DataReliabilityDTO toUpdate)
			throws EntityNotFoundException;

}