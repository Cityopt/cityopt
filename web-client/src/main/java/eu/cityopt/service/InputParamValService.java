package eu.cityopt.service;

import eu.cityopt.DTO.InputParamValDTO;

public interface InputParamValService extends CityOptService<InputParamValDTO> {

	public InputParamValDTO save(InputParamValDTO u);		

	public InputParamValDTO update(InputParamValDTO toUpdate)  throws EntityNotFoundException;
	
	InputParamValDTO findByInputAndScenario(int inParamID, int scenID);
	
}