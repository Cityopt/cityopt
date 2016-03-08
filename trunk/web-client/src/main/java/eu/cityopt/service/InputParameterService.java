package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;

public interface InputParameterService extends CityOptService<InputParameterDTO> {

//	List<InputParameterDTO> findAll();

    InputParameterDTO save(InputParameterDTO u, int componentId, int unitId, TimeSeriesDTOX defaultTimeSeries);

//	void delete(int id) throws EntityNotFoundException;

	InputParameterDTO update(InputParameterDTO toUpdate, int componentId, int unitId, TimeSeriesDTOX defaultTimeSeries) throws EntityNotFoundException;
	
	InputParameterDTO findByID(int id) throws EntityNotFoundException;
	
	Set<InputParamValDTO> getInputParamVals(int id);
	
	int getComponentId(int inputParamId);
	
	List<InputParameterDTO> findByName(String name);
}
