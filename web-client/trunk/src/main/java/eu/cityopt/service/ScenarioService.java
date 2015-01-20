package eu.cityopt.service;

import java.util.Set;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.ScenarioDTO;

public interface ScenarioService extends CityOptService<ScenarioDTO>{
	
//	List<Scenario> findByCreationDate(Date dateLower, Date dateUpper);

	Set<InputParamValDTO> getInputParamVals(int scenId);
	
	ScenarioDTO save(ScenarioDTO s, int prjid);

	void delete(int id) throws EntityNotFoundException;

	ScenarioDTO update(ScenarioDTO toUpdate, int prjid) throws EntityNotFoundException;
	
}