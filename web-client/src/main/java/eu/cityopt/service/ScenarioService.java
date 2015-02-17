package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioMetricsDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationResult;

public interface ScenarioService extends CityOptService<ScenarioDTO>{
	
//	List<Scenario> findByCreationDate(Date dateLower, Date dateUpper);

	Set<InputParamValDTO> getInputParamVals(int scenId);
	
	ScenarioDTO save(ScenarioDTO s, int prjid);

	void delete(int id) throws EntityNotFoundException;

	ScenarioDTO update(ScenarioDTO toUpdate, int prjid) throws EntityNotFoundException;
	
	Set<ScenarioMetricsDTO> getScenarioMetrics(int scenId);
	
	List<ScenarioDTO> findByName(String name);
	
	Set<SimulationResultDTO> getSimulationResults(int scenId);
}