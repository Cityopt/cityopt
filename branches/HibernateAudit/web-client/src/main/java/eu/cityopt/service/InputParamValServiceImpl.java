package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.InputParamVal;
import eu.cityopt.repository.InputParamValRepository;

@Service("InputParamValService")
public class InputParamValServiceImpl implements InputParamValService {
	
	@Autowired
	private InputParamValRepository inputParamValRepository;
	
	public List<InputParamVal> findAll() {
		return inputParamValRepository.findAll();
	}

	@Transactional
	public InputParamVal save(InputParamVal u) {
		return inputParamValRepository.save(u);
	}

	@Transactional
	public void delete(InputParamVal u) throws EntityNotFoundException {
		
		if(inputParamValRepository.findOne(u.getScendefinitionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		inputParamValRepository.delete(u);
	}
	
	@Transactional
	public InputParamVal update(InputParamVal toUpdate) throws EntityNotFoundException {
		
		if(inputParamValRepository.findOne(toUpdate.getScendefinitionid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public InputParamVal findByID(Integer id) {
		return inputParamValRepository.findOne(id);
	}
	
}