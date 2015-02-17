package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.model.SimulationResult;

public interface SimulationResultService extends CityOptService<SimulationResultDTO> {
	List<TimeSeriesValDTO> getTimeSeriesValsOrderedByTime(int scenResId) throws EntityNotFoundException;
}