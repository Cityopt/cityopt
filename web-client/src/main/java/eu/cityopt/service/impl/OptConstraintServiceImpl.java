package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.OptConstraintService;

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
	public OptConstraintDTO update(OptConstraintDTO u) throws EntityNotFoundException {
		
		if(optConstraintRepository.findOne(u.getOptconstid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(u);
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

	@Transactional
	@Override
	public OptConstraintDTO findByNameAndProject(String name, int prjid)
			throws EntityNotFoundException {
		
		OptConstraint oc = optConstraintRepository.findByNameAndProject_prjid(name, prjid);
		if(oc == null) {
			throw new EntityNotFoundException();
		}

		return modelMapper.map(oc, OptConstraintDTO.class);
	}
	
}
