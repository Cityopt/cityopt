package eu.cityopt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.UserGroupProject;
import eu.cityopt.repository.UserGroupProjectRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.UserGroupProjectService;

@Service("UserGroupProjectService")
public class UserGroupProjectServiceImpl implements UserGroupProjectService {
	
	@Autowired
	private UserGroupProjectRepository userGroupProjectRepository;
	
	@Transactional(readOnly=true)
	public List<UserGroupProject> findAll() {
		return userGroupProjectRepository.findAll();
	}

	@Transactional
	public UserGroupProject save(UserGroupProject u) {
		return userGroupProjectRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(userGroupProjectRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		userGroupProjectRepository.delete(id);
	}
	
	@Transactional
	public UserGroupProject update(UserGroupProject toUpdate) throws EntityNotFoundException {
		
		if(userGroupProjectRepository.findOne(toUpdate.getUsergroupprojectid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly=true)
	public UserGroupProject findByID(int id) {
		return userGroupProjectRepository.findOne(id);
	}
	
}