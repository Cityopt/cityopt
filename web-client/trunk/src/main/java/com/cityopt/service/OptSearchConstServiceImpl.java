package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.OptSearchConst;
import com.cityopt.repository.OptSearchConstRepository;

@Service("OptSearchConstService")
public class OptSearchConstServiceImpl implements OptSearchConstService {
	
	@Autowired
	private OptSearchConstRepository optSearchConstRepository;
	
	public List<OptSearchConst> findAll() {
		return optSearchConstRepository.findAll();
	}

	@Transactional
	public OptSearchConst save(OptSearchConst u) {
		return optSearchConstRepository.save(u);
	}

	@Transactional
	public void delete(OptSearchConst u) {
		optSearchConstRepository.delete(u);
	}
	
	public OptSearchConst findByID(Integer id) {
		return optSearchConstRepository.findOne(id);
	}
	
}
