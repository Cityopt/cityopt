package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;

public interface ExtParamService extends CityOptService<ExtParamDTO> {
	
	public ExtParamDTO save(ExtParamDTO u, int prjid);
	
	public ExtParamDTO update(ExtParamDTO toUpdate, int prjid) throws EntityNotFoundException;
	
	public ExtParamDTO findByID(int id) throws EntityNotFoundException;
	
	public Set<ExtParamValDTO> getExtParamVals(int id);
	
	List<ExtParamDTO> findByName(String name);
	
	ExtParamDTO findByName(String name,int prjid);
}