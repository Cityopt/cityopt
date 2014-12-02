package com.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.UserGroupProject;
import com.cityopt.repository.UserGroupProjectRepository;

@Service("UserGroupProjectService")
public class UserGroupProjectServiceImpl implements UserGroupProjectService {
	
	@Autowired
	private UserGroupProjectRepository userGroupProjectRepository;
	
	public List<UserGroupProject> findAll() {
		return userGroupProjectRepository.findAll();
	}

	@Transactional
	public UserGroupProject save(UserGroupProject u) {
		return userGroupProjectRepository.save(u);
	}

	@Transactional
	public void delete(UserGroupProject u) throws EntityNotFoundException {
		
		if(userGroupProjectRepository.findOne(u.getUsergroupprojectid()) == null) {
			throw new EntityNotFoundException();
		}
		
		userGroupProjectRepository.delete(u);
	}
	
	@Transactional
	public UserGroupProject update(UserGroupProject toUpdate) throws EntityNotFoundException {
		
		if(userGroupProjectRepository.findOne(toUpdate.getUsergroupprojectid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	public UserGroupProject findByID(Integer id) {
		return userGroupProjectRepository.findOne(id);
	}
	
}
