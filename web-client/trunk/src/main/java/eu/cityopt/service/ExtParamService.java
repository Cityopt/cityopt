package eu.cityopt.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.Project;

public interface ExtParamService extends CityOptService<ExtParamDTO> {
	
	public ExtParamDTO save(ExtParamDTO u, int prjid);
	
	public ExtParamDTO update(ExtParamDTO toUpdate, int prjid) throws EntityNotFoundException;
	
	public ExtParamDTO findByID(int id) throws EntityNotFoundException;
	

}