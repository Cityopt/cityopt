package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ComponentInputParamDTO;

public interface ComponentInputParamDTOService {

	List<ComponentInputParamDTO> findAllByPrjAndScenId(int prjid, int scenid);
	
}
