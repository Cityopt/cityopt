package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;

public interface ExtParamValSetService extends CityOptService<ExtParamValSetDTO>{
	
	public ExtParamValSetDTO save(ExtParamValSetDTO epvs);
	
	public List<ExtParamValDTO> getExtParamVals(int extParamValSetId) 
			throws EntityNotFoundException;
	
	public void addExtParamVals(int extParamValSetId, Set<ExtParamValDTO> epVals) 
			throws EntityNotFoundException;	
}
