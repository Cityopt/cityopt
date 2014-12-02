package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.AlgoParamVal;
import com.cityopt.repository.AlgoParamValRepository;

@Service("AlgoParamValService")
public class AlgoParamValServiceImpl implements AlgoParamValService{
	
	@Autowired
	private AlgoParamValRepository algoParamValRepository;
	
	public List<AlgoParamVal> findAll() {
		return algoParamValRepository.findAll();
	}

	@Transactional
	public AlgoParamVal save(AlgoParamVal u) {
		return algoParamValRepository.save(u);
	}
	
	@Transactional
	public void delete(AlgoParamVal u) throws EntityNotFoundException {
		
		if(algoParamValRepository.findOne(u.getAparamvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		algoParamValRepository.delete(u);
	}
	
	@Transactional
	public AlgoParamVal update(AlgoParamVal toUpdate) throws EntityNotFoundException {
		
		if(algoParamValRepository.findOne(toUpdate.getAparamvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public AlgoParamVal findByID(Integer id) {
		return algoParamValRepository.findOne(id);
	}
	
}
