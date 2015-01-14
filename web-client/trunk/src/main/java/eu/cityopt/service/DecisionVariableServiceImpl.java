package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.DecisionVariable;
import eu.cityopt.repository.DecisionVariableRepository;

@Service("DecisionVariableService")
public class DecisionVariableServiceImpl implements DecisionVariableService {
	
	@Autowired
	private DecisionVariableRepository decisionVariableRepository;
	
	public List<DecisionVariable> findAll() {
		return decisionVariableRepository.findAll();
	}

	@Transactional
	public DecisionVariable save(DecisionVariable u) {
		return decisionVariableRepository.save(u);
	}

	@Transactional
	public void delete(Integer id) throws EntityNotFoundException {
		
		if(decisionVariableRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		decisionVariableRepository.delete(id);
	}
	
	@Transactional
	public DecisionVariable update(DecisionVariable toUpdate) throws EntityNotFoundException {
		
		if(decisionVariableRepository.findOne(toUpdate.getDecisionvarid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public DecisionVariable findByID(Integer id) {
		return decisionVariableRepository.findOne(id);
	}
	
}
