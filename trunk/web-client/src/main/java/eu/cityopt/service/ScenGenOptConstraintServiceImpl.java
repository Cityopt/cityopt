package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ScenGenOptConstraint;
import eu.cityopt.repository.ScenGenOptConstraintRepository;

@Service("ScenGenOptConstraintService")
public class ScenGenOptConstraintServiceImpl implements ScenGenOptConstraintService {
	
	@Autowired
	private ScenGenOptConstraintRepository scenGenOptConstraintRepository;
	
	@Transactional(readOnly=true)
	public List<ScenGenOptConstraint> findAll() {
		return scenGenOptConstraintRepository.findAll();
	}

	@Transactional
	public ScenGenOptConstraint save(ScenGenOptConstraint u) {
		return scenGenOptConstraintRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(scenGenOptConstraintRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		scenGenOptConstraintRepository.delete(id);
	}
	
	@Transactional
	public ScenGenOptConstraint update(ScenGenOptConstraint toUpdate) throws EntityNotFoundException {
		
		if(scenGenOptConstraintRepository.findOne(toUpdate.getSgoptconstraintid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public ScenGenOptConstraint findByID(int id) {
		return scenGenOptConstraintRepository.findOne(id);
	}
	
}