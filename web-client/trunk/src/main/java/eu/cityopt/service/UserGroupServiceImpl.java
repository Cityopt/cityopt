package eu.cityopt.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.cityopt.model.UserGroup;
import eu.cityopt.repository.UserGroupRepository;

@Service("UserGroupService")
public class UserGroupServiceImpl implements UserGroupService {
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	public List<UserGroup> findAll() {
		return userGroupRepository.findAll();
	}
	
	public UserGroup findByID(Integer id) {
		return userGroupRepository.findOne(id);
	}
	
	public List<UserGroup> findByGroupName(String userGroupName) {
		return userGroupRepository.findByGroupName(userGroupName);
	}
	
	@Transactional
	public UserGroup save(UserGroup model) {
		return userGroupRepository.save(model);
	}
	
	@Transactional
	public void delete(Integer id) throws EntityNotFoundException {
		
		if(userGroupRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		userGroupRepository.delete(id);
	}

	@Transactional
	public UserGroup update(UserGroup toUpdate) throws EntityNotFoundException {
		
		if(userGroupRepository.findOne(toUpdate.getUsergroupid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
}
