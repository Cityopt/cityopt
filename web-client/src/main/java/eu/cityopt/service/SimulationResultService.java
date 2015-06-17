package eu.cityopt.service;

import java.util.List;

import org.springframework.data.domain.Page;

import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.model.SimulationResult;

public interface SimulationResultService extends CityOptService<SimulationResultDTO> {
	List<TimeSeriesValDTO> getTimeSeriesValsOrderedByTime(int scenResId) throws EntityNotFoundException;
	
	SimulationResultDTO findByOutVarIdScenId(int outVarId, int scenarioID) throws EntityNotFoundException;
	
	Page<SimulationResultDTO> findAll(int pageSize);
}