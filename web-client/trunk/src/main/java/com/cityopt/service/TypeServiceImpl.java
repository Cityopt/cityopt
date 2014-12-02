package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.Type;
import com.cityopt.repository.TypeRepository;

@Service("TypeService")
public class TypeServiceImpl implements TypeService {
	
	@Autowired
	private TypeRepository typeRepository;
	
	public List<Type> findAll() {
		return typeRepository.findAll();
	}

	@Transactional
	public Type save(Type u) {
		return typeRepository.save(u);
	}

	@Transactional
	public void delete(Type u) throws EntityNotFoundException {
		
		if(typeRepository.findOne(u.getTypeid()) == null) {
			throw new EntityNotFoundException();
		}
		
		typeRepository.delete(u);
	}
	
	@Transactional
	public Type update(Type toUpdate) throws EntityNotFoundException {
		
		if(typeRepository.findOne(toUpdate.getTypeid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public Type findByID(Integer id) {
		return typeRepository.findOne(id);
	}
	
}
