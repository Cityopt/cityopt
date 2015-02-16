package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.OutputVariable;
import eu.cityopt.repository.OutputVariableRepository;

@Service("OutputVariableService")
public class OutputVariableServiceImpl implements OutputVariableService {
	
	@Autowired
	private OutputVariableRepository outputVariableRepository;
	
	public List<OutputVariable> findAll() {
		return outputVariableRepository.findAll();
	}

	@Transactional
	public OutputVariable save(OutputVariable u) {
		return outputVariableRepository.save(u);
	}

	@Transactional
	public void delete(OutputVariable u) throws EntityNotFoundException {
		
		if(outputVariableRepository.findOne(u.getOutvarid()) == null) {
			throw new EntityNotFoundException();
		}
		
		outputVariableRepository.delete(u);
	}
	
	@Transactional
	public OutputVariable update(OutputVariable toUpdate) throws EntityNotFoundException {
		
		if(outputVariableRepository.findOne(toUpdate.getOutvarid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public OutputVariable findByID(Integer id) {
		return outputVariableRepository.findOne(id);
	}
	
}
