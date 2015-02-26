package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.UserGroup;
import eu.cityopt.repository.UserGroupRepository;

@Service("UserGroupService")
public class UserGroupServiceImpl implements UserGroupService {
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	@Transactional(readOnly=true)
	public List<UserGroup> findAll() {
		return userGroupRepository.findAll();
	}
	
	@Transactional(readOnly=true)
	public UserGroup findByID(int id) {
		return userGroupRepository.findOne(id);
	}
	
	@Transactional(readOnly=true)
	public List<UserGroup> findByGroupName(String userGroupName) {
		return userGroupRepository.findByGroupName(userGroupName);
	}
	
	@Transactional
	public UserGroup save(UserGroup model) {
		return userGroupRepository.save(model);
	}
	
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
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
