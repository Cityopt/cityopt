package eu.cityopt.service.impl;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Unit;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.InputParameterRepository;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.InputParameterService;

@Service("InputParameterService")
public class InputParameterServiceImpl implements InputParameterService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private InputParameterRepository inputParameterRepository;
	
	@Autowired
	private UnitRepository unitRepository;
	
	@Autowired
	private ComponentRepository componentRepository;
	
	@Transactional(readOnly = true)
	@Override
	public List<InputParameterDTO> findAll() {
		return modelMapper.map(inputParameterRepository.findAll(), 
				new TypeToken<List<InputParameterDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<InputParameterDTO> findByName(String name) {
		List<InputParameter> iparams = inputParameterRepository.findByNameContaining(name);
		List<InputParameterDTO> result 
			= modelMapper.map(iparams, new TypeToken<List<InputParameterDTO>>() {}.getType());
		return result;
	}

	@Transactional
	@Override
	public InputParameterDTO save(InputParameterDTO u, int componentId, int unitId) {
		InputParameter param = modelMapper.map(u, InputParameter.class);
		Component com = componentRepository.getOne(componentId);
		Unit unit = unitRepository.getOne(unitId);
		
		param.setComponent(com);
		param.setUnit(unit);
		param = inputParameterRepository.save(param);
		return modelMapper.map(param, InputParameterDTO.class);
	}

	@Transactional
	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(inputParameterRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		inputParameterRepository.delete(id);
	}
	
	@Transactional
	@Override
	public InputParameterDTO update(InputParameterDTO toUpdate, int componentId, int unitId) throws EntityNotFoundException {
		
		if(inputParameterRepository.findOne(toUpdate.getInputid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate, componentId, unitId);
	}
	
	@Transactional(readOnly = true)
	@Override
	public InputParameterDTO findByID(int id) throws EntityNotFoundException {
		
		if(inputParameterRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(inputParameterRepository.findOne(id), InputParameterDTO.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Set<InputParamValDTO> getInputParamVals(int id){
		InputParameter iparam = inputParameterRepository.findOne(id);
		Set<InputParamVal> inputParamVals = iparam.getInputparamvals();
		return modelMapper.map(inputParamVals, new TypeToken<Set<InputParamValDTO>>() {}.getType());
	}
}
