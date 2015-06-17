package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Unit;
import eu.cityopt.repository.InputParamValRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.InputParamValService;

@Service("InputParamValService")
public class InputParamValServiceImpl implements InputParamValService {
	private static final int PAGE_SIZE = 50;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private InputParamValRepository inputParamValRepository;
	
	@Override
	@Transactional(readOnly = true)
	public List<InputParamValDTO> findAll() {
		return modelMapper.map(inputParamValRepository.findAll(), 
				new TypeToken<List<InputParamValDTO>>() {}.getType());
	}

	@Override
	@Transactional
	public InputParamValDTO save(InputParamValDTO u) {
		InputParamVal paramVal = modelMapper.map(u, InputParamVal.class);
		paramVal = inputParamValRepository.save(paramVal);
		return modelMapper.map(paramVal, InputParamValDTO.class);		
	}

	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(inputParamValRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		inputParamValRepository.delete(id);
	}
	
	@Override
	@Transactional
	public InputParamValDTO update(InputParamValDTO toUpdate) throws EntityNotFoundException {
		
		if(inputParamValRepository.findOne(toUpdate.getInputparamvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Override
	@Transactional(readOnly = true)
	public InputParamValDTO findByID(int id) throws EntityNotFoundException {
		
		InputParamVal iparVal = inputParamValRepository.findOne(id);
		
		if(iparVal == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(iparVal, InputParamValDTO.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public InputParamValDTO findByInputAndScenario(int inParamID, int scenID) {
		InputParamVal ipv = inputParamValRepository.findByInputIdAndScenId(inParamID, scenID);
		
		return ipv != null ? modelMapper.map(ipv, InputParamValDTO.class) : null;
	}
	
	@Transactional(readOnly = true)
	@Override
	public InputParamValDTO findByNameAndScenario(String name, int scenId) {
		InputParamVal iparams = inputParamValRepository.findByNameAndScenario(name, scenId);
		InputParamValDTO result 
			= modelMapper.map(iparams, InputParamValDTO.class);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<InputParamValDTO> findByComponentAndScenario(int componentID,
			int scenID) {
		List<InputParamVal> ipvList = inputParamValRepository.findByComponentAndScenario(componentID, scenID);
		
		return modelMapper.map(ipvList, new TypeToken<List<InputParamValDTO>>() {}.getType());
	}

	@Override
	public Page<InputParamValDTO> findByComponentAndScenario(int componentID,
			int scenID, int pageIndex) {
		
		PageRequest request =
	            new PageRequest(pageIndex,PAGE_SIZE);
		
		Page<InputParamVal> ipvList = inputParamValRepository.findByComponentAndScenario(componentID, scenID,request);		
		
		return modelMapper.map(ipvList, new TypeToken<Page<InputParamValDTO>>() {}.getType());		
		
	}
	
}
