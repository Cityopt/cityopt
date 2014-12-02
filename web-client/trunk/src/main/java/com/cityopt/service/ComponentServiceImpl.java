package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.Component;
import com.cityopt.repository.ComponentRepository;

@Service("ComponentService")
public class ComponentServiceImpl implements ComponentService {
	
	@Autowired
	private ComponentRepository componentRepository;
	
	public List<Component> findAll() {
		return componentRepository.findAll();
	}

	@Transactional
	public Component save(Component u) {
		return componentRepository.save(u);
	}

	@Transactional
	public void delete(Component u) throws EntityNotFoundException {
		
		if(componentRepository.findOne(u.getComponentid()) == null) {
			throw new EntityNotFoundException();
		}
		
		componentRepository.delete(u);
	}
	
	@Transactional
	public Component update(Component toUpdate) throws EntityNotFoundException {
		
		if(componentRepository.findOne(toUpdate.getComponentid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public Component findByID(Integer id) {
		return componentRepository.findOne(id);
	}
	
}
