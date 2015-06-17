package eu.cityopt.service;

import java.util.List;

import org.springframework.data.domain.Page;

import eu.cityopt.DTO.TimeSeriesValDTO;

public interface TimeSeriesValService extends CityOptService<TimeSeriesValDTO> {

	List<TimeSeriesValDTO> findByTimeSeriesIdOrderedByTime(int timeSeriesId)	
			throws EntityNotFoundException;	

	Page<TimeSeriesValDTO> findByTimeSeriesIdOrderedByTime(int timeSeriesId,int pageIndex)	
			throws EntityNotFoundException;	
}