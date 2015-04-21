package eu.cityopt.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.repository.OptConstraintRepository;

@Service("OptConstraintService")
public class OptConstraintServiceImpl implements OptConstraintService {
	
	@Autowired
	private OptConstraintRepository optConstraintRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	@Transactional(readOnly=true)
	public List<OptConstraintDTO> findAll() {
		return modelMapper.map(optConstraintRepository.findAll(), 
				new TypeToken<List<OptConstraintDTO>>(){}.getType());
	}
	
	@Override
	@Transactional
	public OptConstraintDTO save(OptConstraintDTO u) {
		OptConstraint oc = modelMapper.map(u, OptConstraint.class);
		
		oc = optConstraintRepository.save(oc);
	
		return modelMapper.map(oc, OptConstraintDTO.class);
	}

	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(optConstraintRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		optConstraintRepository.delete(id);
	}
	
	@Override
	@Transactional(readOnly=true)
	public OptConstraintDTO findByID(int id) throws EntityNotFoundException {
		OptConstraint oc = optConstraintRepository.findOne(id);
		
		if(oc == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(oc, OptConstraintDTO.class);
	}
	
}
