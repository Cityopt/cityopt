package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.ExtParamVal;
import com.cityopt.repository.ExtParamValRepository;

@Service("ExtParamValService")
public class ExtParamValServiceImpl implements ExtParamValService {
	
	@Autowired
	private ExtParamValRepository extParamValRepository;
	
	public List<ExtParamVal> findAll() {
		return extParamValRepository.findAll();
	}

	@Transactional
	public ExtParamVal save(ExtParamVal u) {
		return extParamValRepository.save(u);
	}

	@Transactional
	public void delete(ExtParamVal u) throws EntityNotFoundException {
		
		if(extParamValRepository.findOne(u.getExtparamvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		extParamValRepository.delete(u);
	}
	
	@Transactional
	public ExtParamVal update(ExtParamVal toUpdate) throws EntityNotFoundException {
		
		if(extParamValRepository.findOne(toUpdate.getExtparamvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public ExtParamVal findByID(Integer id) {
		return extParamValRepository.findOne(id);
	}
	
}
