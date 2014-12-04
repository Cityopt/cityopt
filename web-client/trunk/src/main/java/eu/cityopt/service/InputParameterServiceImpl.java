package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.InputParameter;
import eu.cityopt.repository.InputParameterRepository;

@Service("InputParameterService")
public class InputParameterServiceImpl implements InputParameterService {
	
	@Autowired
	private InputParameterRepository inputParameterRepository;
	
	public List<InputParameter> findAll() {
		return inputParameterRepository.findAll();
	}

	@Transactional
	public InputParameter save(InputParameter u) {
		return inputParameterRepository.save(u);
	}

	@Transactional
	public void delete(InputParameter u) throws EntityNotFoundException {
		
		if(inputParameterRepository.findOne(u.getInputid()) == null) {
			throw new EntityNotFoundException();
		}
		
		inputParameterRepository.delete(u);
	}
	
	@Transactional
	public InputParameter update(InputParameter toUpdate) throws EntityNotFoundException {
		
		if(inputParameterRepository.findOne(toUpdate.getInputid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public InputParameter findByID(Integer id) {
		return inputParameterRepository.findOne(id);
	}
	
}
