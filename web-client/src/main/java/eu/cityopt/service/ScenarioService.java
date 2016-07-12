package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioMetricsDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationResult;

public interface ScenarioService extends CityOptService<ScenarioDTO>{
	
//	List<Scenario> findByCreationDate(Date dateLower, Date dateUpper);

	List<InputParamValDTO> getInputParamVals(int scenId);
	
	ScenarioDTO save(ScenarioDTO s, int prjid);

	void delete(int id) throws EntityNotFoundException;

	ScenarioDTO update(ScenarioDTO toUpdate, int prjid) throws EntityNotFoundException;
	
	List<ScenarioMetricsDTO> getScenarioMetrics(int scenId);
	
	List<ScenarioDTO> findByNameContaining(String name);
	
	ScenarioDTO findByNameAndProject(int prjid, String name);
	
	ScenarioDTO findByName(String name);
	
	List<SimulationResultDTO> getSimulationResults(int scenId);

	Set<MetricValDTO> getMetricsValues(int scenId);

	/**
	 * saves the scenario s and creates default inputparametervalues for all components in the project
	 * 
	 * @param s
	 * @param prjid
	 * @return
	 * @throws EntityNotFoundException 
	 */
	ScenarioDTO saveWithDefaultInputValues(ScenarioDTO s, int prjid) throws EntityNotFoundException;
}