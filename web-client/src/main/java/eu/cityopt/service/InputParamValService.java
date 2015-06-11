package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;

public interface InputParamValService extends CityOptService<InputParamValDTO> {

	public InputParamValDTO save(InputParamValDTO u);		

	public InputParamValDTO update(InputParamValDTO toUpdate)  throws EntityNotFoundException;
	
	InputParamValDTO findByInputAndScenario(int inParamID, int scenID);
	
	List<InputParamValDTO> findByComponentAndScenario(int componentID, int scenID);

	List<InputParameterDTO> findByNameAndScenario(String name, int scenId);
	
}