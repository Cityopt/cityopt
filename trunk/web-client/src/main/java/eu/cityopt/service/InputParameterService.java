package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;

public interface InputParameterService extends CityOptService<InputParameterDTO>{
	
//	List<InputParameterDTO> findAll();

	InputParameterDTO save(InputParameterDTO u, int componentId, int unitId);

//	void delete(int id) throws EntityNotFoundException;
		
	InputParameterDTO update(InputParameterDTO toUpdate, int componentId, int unitId) throws EntityNotFoundException;
	
	InputParameterDTO findByID(int id) throws EntityNotFoundException;
	
	InputParamValDTO findByInputAndScenario(int inParamID, int scenID);
	
	Set<InputParamValDTO> getInputParamVals(int id);
	
	List<InputParameterDTO> findByName(String name);
}