package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.AlgoParamValDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.model.AlgoParamVal;
import eu.cityopt.model.ModelParameter;
import eu.cityopt.repository.ModelParameterRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ModelParameterService;

@Service("ModelParameterService")
public class ModelParameterServiceImpl implements ModelParameterService {
	
	@Autowired
	private ModelParameterRepository modelParameterRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	@Transactional(readOnly=true)
	public List<ModelParameterDTO> findAll() {
		return modelMapper.map(modelParameterRepository.findAll(), 
				new TypeToken<List<ModelParameterDTO>>(){}.getType());
		
	}

	@Override
	@Transactional
	public ModelParameterDTO save(ModelParameterDTO u) {
		ModelParameter modelParam = modelMapper.map(u, ModelParameter.class);
		modelParam = modelParameterRepository.save(modelParam);
		return modelMapper.map(modelParam, ModelParameterDTO.class);
	}

	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(modelParameterRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		modelParameterRepository.delete(id);
	}
	
	@Override
	@Transactional
	public ModelParameterDTO update(ModelParameterDTO toUpdate) throws EntityNotFoundException {
		
		if(modelParameterRepository.findOne(toUpdate.getModelparamid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public ModelParameterDTO findByID(int id) throws EntityNotFoundException {
		ModelParameter modelParam = modelParameterRepository.findOne(id);
		
		if(modelParam == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(modelParam, ModelParameterDTO.class);
	}
	
}
