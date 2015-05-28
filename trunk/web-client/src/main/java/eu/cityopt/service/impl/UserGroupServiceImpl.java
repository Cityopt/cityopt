package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.UserGroupDTO;
import eu.cityopt.model.UserGroup;
import eu.cityopt.repository.UserGroupRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.UserGroupService;

@Service("UserGroupService")
public class UserGroupServiceImpl implements UserGroupService {
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Transactional(readOnly=true)
	@Override
	public List<UserGroupDTO> findAll() {
		return modelMapper.map(userGroupRepository.findAll(), 
				new TypeToken<List<UserGroupDTO>>() {}.getType());
	}
	
	@Transactional(readOnly=true)
	@Override
	public UserGroupDTO findByID(int id) throws EntityNotFoundException {
		UserGroup ug = userGroupRepository.findOne(id);
		
		if(ug == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(ug, UserGroupDTO.class);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<UserGroupDTO> findByGroupNameContaining(String userGroupName) {
		List<UserGroup> ug = userGroupRepository.findByGroupNameContaining(userGroupName);
		
		return  modelMapper.map(ug,
				new TypeToken<List<UserGroupDTO>>() {}.getType());
	}
	
	@Transactional
	@Override
	public UserGroupDTO save(UserGroupDTO u) {
		UserGroup ug = modelMapper.map(u, UserGroup.class);
		ug = userGroupRepository.save(ug);
		return modelMapper.map(ug, UserGroupDTO.class);
	}
	
	@Transactional
	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(userGroupRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		userGroupRepository.delete(id);
	}

	@Transactional
	@Override
	public UserGroupDTO update(UserGroupDTO toUpdate) throws EntityNotFoundException {
		
		if(userGroupRepository.findOne(toUpdate.getUsergroupid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
}
