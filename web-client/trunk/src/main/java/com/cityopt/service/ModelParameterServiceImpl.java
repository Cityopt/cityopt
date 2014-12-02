package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ModelParameter;
import com.cityopt.repository.ModelParameterRepository;

@Service("ModelParameterService")
public class ModelParameterServiceImpl implements ModelParameterService {
	
	@Autowired
	private ModelParameterRepository modelParameterRepository;
	
	public List<ModelParameter> findAll() {
		return modelParameterRepository.findAll();
	}

	@Transactional
	public ModelParameter save(ModelParameter u) {
		return modelParameterRepository.save(u);
	}

	@Transactional
	public void delete(ModelParameter u) throws EntityNotFoundException {
		
		if(modelParameterRepository.findOne(u.getModelparamid()) == null) {
			throw new EntityNotFoundException();
		}
		
		modelParameterRepository.delete(u);
	}
	
	@Transactional
	public ModelParameter update(ModelParameter toUpdate) throws EntityNotFoundException {
		
		if(modelParameterRepository.findOne(toUpdate.getModelparamid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ModelParameter findByID(Integer id) {
		return modelParameterRepository.findOne(id);
	}
	
}
