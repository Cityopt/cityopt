package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.ComponentInputParamDTO;
import eu.cityopt.repository.CustomQueryRepository;

@Service
public class ComponentInputParamDTOServiceImpl implements ComponentInputParamDTOService {

	@Autowired
	private CustomQueryRepository cqRepository;
	
	@Override
	@Transactional(readOnly=true)
	public List<ComponentInputParamDTO> findAllByPrjAndScenId(int prjid, int scenid) {
		return cqRepository.findComponentsWithInputParams(prjid, scenid);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ComponentInputParamDTO> findAllByComponentId(int componentId) {
		return cqRepository.findComponentsWithInputParamsByCompId(componentId);
	}
	
}
