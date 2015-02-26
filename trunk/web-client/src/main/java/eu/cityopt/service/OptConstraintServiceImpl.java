package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.OptConstraint;
import eu.cityopt.repository.OptConstraintRepository;

@Service("OptConstraintService")
public class OptConstraintServiceImpl implements OptConstraintService {
	
	@Autowired
	private OptConstraintRepository optConstraintRepository;
	
	@Transactional(readOnly=true)
	public List<OptConstraint> findAll() {
		return optConstraintRepository.findAll();
	}

	@Transactional
	public OptConstraint save(OptConstraint u) {
		return optConstraintRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(optConstraintRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		optConstraintRepository.delete(id);
	}
	
	@Transactional
	public OptConstraint update(OptConstraint toUpdate) throws EntityNotFoundException {
		
		if(optConstraintRepository.findOne(toUpdate.getOptconstid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public OptConstraint findByID(int id) {
		return optConstraintRepository.findOne(id);
	}
	
}
