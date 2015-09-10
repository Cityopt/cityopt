package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.OptSetScenariosDTO;
import eu.cityopt.model.OptSetScenarios;
import eu.cityopt.repository.OptSetScenariosRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.OptSetScenariosService;

@Service
@Transactional
public class OptSetScenariosServiceImpl implements OptSetScenariosService {
	
	@Autowired
	private OptSetScenariosRepository optSetScenariosRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Transactional
	@Override
	public List<OptSetScenariosDTO> findAll() {
		return modelMapper.map(optSetScenariosRepository.findAll(), 
				new TypeToken<List<OptSetScenariosDTO>>() {}.getType());
	}
	
	@Transactional
	@Override
	public OptSetScenariosDTO findByID(int id) throws EntityNotFoundException {
		OptSetScenarios oss = optSetScenariosRepository.findOne(id);
		if(oss == null) {
			throw new EntityNotFoundException();
		}
		return modelMapper.map(oss, OptSetScenariosDTO.class);
	}

	@Transactional
	@Override
	public OptSetScenariosDTO save(OptSetScenariosDTO u) {
		OptSetScenarios oss = modelMapper.map(u, OptSetScenarios.class);
		oss = optSetScenariosRepository.save(oss);		
		return modelMapper.map(oss, OptSetScenariosDTO.class);
	}

	@Transactional
	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(optSetScenariosRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		optSetScenariosRepository.delete(id);
	}
	
	@Transactional
	@Override
	public OptSetScenariosDTO update(OptSetScenariosDTO toUpdate) throws EntityNotFoundException {
		OptSetScenarios oss = optSetScenariosRepository.findOne(toUpdate.getOptscenid());
		
		if(oss == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
}