package eu.cityopt.service;

import java.util.List;

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
import eu.cityopt.repository.InputParamValRepository;

@Service("InputParamValService")
public class InputParamValServiceImpl implements InputParamValService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private InputParamValRepository inputParamValRepository;
	
	@Transactional(readOnly = true)
	public List<InputParamValDTO> findAll() {
		return modelMapper.map(inputParamValRepository.findAll(), 
				new TypeToken<List<InputParamValDTO>>() {}.getType());
	}

	@Transactional
	public InputParamValDTO save(InputParamValDTO u) {
		InputParamVal paramVal = modelMapper.map(u, InputParamVal.class);
		paramVal = inputParamValRepository.save(paramVal);
		return modelMapper.map(paramVal, InputParamValDTO.class);		
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(inputParamValRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		inputParamValRepository.delete(id);
	}
	
	@Transactional
	public InputParamValDTO update(InputParamValDTO toUpdate) throws EntityNotFoundException {
		
		if(inputParamValRepository.findOne(toUpdate.getScendefinitionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly = true)
	public InputParamValDTO findByID(int id) throws EntityNotFoundException {
		
		InputParamVal iparVal = inputParamValRepository.findOne(id);
		
		if(iparVal == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(iparVal, InputParamValDTO.class);
	}
	
}
