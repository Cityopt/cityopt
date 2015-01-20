package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ScenGenObjectiveFunction;
import eu.cityopt.repository.ScenGenObjectiveFunctionRepository;

@Service("ScenGenObjectiveFunctionService")
public class ScenGenObjectiveFunctionServiceImpl implements ScenGenObjectiveFunctionService {
	
	@Autowired
	private ScenGenObjectiveFunctionRepository scenGenObjectiveFunctionRepository;
	
	public List<ScenGenObjectiveFunction> findAll() {
		return scenGenObjectiveFunctionRepository.findAll();
	}

	@Transactional
	public ScenGenObjectiveFunction save(ScenGenObjectiveFunction u) {
		return scenGenObjectiveFunctionRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(scenGenObjectiveFunctionRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		scenGenObjectiveFunctionRepository.delete(id);
	}
	
	@Transactional
	public ScenGenObjectiveFunction update(ScenGenObjectiveFunction toUpdate) throws EntityNotFoundException {
		
		if(scenGenObjectiveFunctionRepository.findOne(toUpdate.getSgobfunctionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ScenGenObjectiveFunction findByID(int id) {
		return scenGenObjectiveFunctionRepository.findOne(id);
	}
	
}
