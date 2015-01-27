package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.cityopt.DTO.ComponentInputParamDTO;
import eu.cityopt.repository.CustomQueryRepository;

@Service
public class ComponentInputParamDTOServiceImpl implements ComponentInputParamDTOService {

	@Autowired
	private CustomQueryRepository cqRepository;
	
	@Override
	public List<ComponentInputParamDTO> findAllByPrjAndScenId(int prjid, int scenid) {
		return cqRepository.findComponentsWithInputParams(prjid, scenid);
	}
	
}
