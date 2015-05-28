package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.DataReliabilityDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.model.DataReliability;
import eu.cityopt.model.DecisionVariable;
import eu.cityopt.repository.DataReliabilityRepository;
import eu.cityopt.service.DataReliabilityService;
import eu.cityopt.service.EntityNotFoundException;

@Service("DataReliabilityService")
@Transactional
public class DataReliabilityServiceImpl implements DataReliabilityService {
	
	@Autowired
	private DataReliabilityRepository dataReliabilityRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public List<DataReliabilityDTO> findAll() {
		return modelMapper.map(dataReliabilityRepository.findAll(), 
				new TypeToken<List<DataReliabilityDTO>>() {}.getType());
	}
	
	@Transactional
	@Override
	public DataReliabilityDTO findByID(int id) {
		return modelMapper.map(dataReliabilityRepository.findOne(id), DataReliabilityDTO.class);
	}

	@Transactional
	@Override
	public DataReliabilityDTO save(DataReliabilityDTO d) {
		DataReliability dr = modelMapper.map(d, DataReliability.class);
		dr = dataReliabilityRepository.save(dr);
		
		return modelMapper.map(dr, DataReliabilityDTO.class);
	}

	@Transactional
	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(dataReliabilityRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		dataReliabilityRepository.delete(id);
	}
	
	@Transactional
	@Override
	public DataReliabilityDTO update(DataReliabilityDTO toUpdate) throws EntityNotFoundException {
		
		if(dataReliabilityRepository.findOne(toUpdate.getDatarelid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
}
