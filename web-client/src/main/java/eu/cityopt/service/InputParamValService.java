package eu.cityopt.service;

import java.util.List;

import org.springframework.data.domain.Page;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;

public interface InputParamValService extends CityOptService<InputParamValDTO> {

	public default InputParamValDTO save(InputParamValDTO u) {
	    return save(u, null);
	}
    InputParamValDTO save(InputParamValDTO u, TimeSeriesDTOX timeSeriesData);       

    public default InputParamValDTO update(InputParamValDTO toUpdate) throws EntityNotFoundException {
        return update(toUpdate, null);
    }
    InputParamValDTO update(InputParamValDTO toUpdate, TimeSeriesDTOX timeSeriesData)
	        throws EntityNotFoundException;
	
	InputParamValDTO findByInputAndScenario(int inParamID, int scenID);
	
	List<InputParamValDTO> findByComponentAndScenario(int componentID, int scenID);
	
	Page<InputParamValDTO> findByComponentAndScenario(int componentID, int scenID,int pageIndex);

	InputParamValDTO findByNameAndScenario(String name, int scenId);
	
}