package eu.cityopt.service;

import java.util.List;

import org.springframework.data.domain.Page;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;

public interface InputParamValService extends CityOptService<InputParamValDTO> {

	public InputParamValDTO save(InputParamValDTO u);		

	public InputParamValDTO update(InputParamValDTO toUpdate)  throws EntityNotFoundException;
	
	InputParamValDTO findByInputAndScenario(int inParamID, int scenID);
	
	List<InputParamValDTO> findByComponentAndScenario(int componentID, int scenID);
	
	Page<InputParamValDTO> findByComponentAndScenario(int componentID, int scenID,int pageIndex);

	InputParamValDTO findByNameAndScenario(String name, int scenId);
	
}