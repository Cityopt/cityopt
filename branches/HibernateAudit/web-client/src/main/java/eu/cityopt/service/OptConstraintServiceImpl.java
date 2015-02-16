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
	
	public List<OptConstraint> findAll() {
		return optConstraintRepository.findAll();
	}

	@Transactional
	public OptConstraint save(OptConstraint u) {
		return optConstraintRepository.save(u);
	}

	@Transactional
	public void delete(OptConstraint u) throws EntityNotFoundException {
		
		if(optConstraintRepository.findOne(u.getOptconstid()) == null) {
			throw new EntityNotFoundException();
		}
		
		optConstraintRepository.delete(u);
	}
	
	@Transactional
	public OptConstraint update(OptConstraint toUpdate) throws EntityNotFoundException {
		
		if(optConstraintRepository.findOne(toUpdate.getOptconstid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public OptConstraint findByID(Integer id) {
		return optConstraintRepository.findOne(id);
	}
	
}
