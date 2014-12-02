package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.AppUser;
import com.cityopt.repository.AppUserRepository;

@Service("AppUserService")
public class AppUserServiceImpl implements AppUserService {
	
	@Autowired
	private AppUserRepository appuserRepository;
	
	public List<AppUser> findAll() {
		return appuserRepository.findAll();
	}

	@Transactional
	public AppUser save(AppUser u) {
		return appuserRepository.save(u);
	}

	@Transactional
	public void deleteAll() {
		appuserRepository.deleteAll();
	}
	
	@Transactional
	public void delete(AppUser u) throws EntityNotFoundException {
		
		if(appuserRepository.findOne(u.getUserid()) == null) {
			throw new EntityNotFoundException();
		}
		
		appuserRepository.delete(u);
	}
	
	@Transactional
	public AppUser update(AppUser toUpdate) throws EntityNotFoundException {
		
		if(appuserRepository.findOne(toUpdate.getUserid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public AppUser findByID(Integer id) {
		return appuserRepository.findOne(id);
	}
	
	public List<AppUser> findByUserName(String name) {
		return appuserRepository.findByUserName(name);
	}
	
	public AppUser authenticateUser(String name, String password) {
		return appuserRepository.authenticateUser(name, password);
	}
	
}
