package eu.cityopt.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.model.UserGroupProject;
import eu.cityopt.repository.UserGroupProjectRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.UserGroupProjectService;

@Service("UserGroupProjectService")
public class UserGroupProjectServiceImpl implements UserGroupProjectService {
	
	@Autowired
	private UserGroupProjectRepository userGroupProjectRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	@Transactional(readOnly=true)
	public List<UserGroupProjectDTO> findAll() {
		return modelMapper.map(userGroupProjectRepository.findAll(), 
				new TypeToken<List<UserGroupProjectDTO>>() {}.getType());
	}

	@Override
	@Transactional
	public UserGroupProjectDTO save(UserGroupProjectDTO u) {
		UserGroupProject ugp = modelMapper.map(u, UserGroupProject.class);
		ugp = userGroupProjectRepository.save(ugp);
		return modelMapper.map(ugp, UserGroupProjectDTO.class);
	}

	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(userGroupProjectRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		userGroupProjectRepository.delete(id);
	}
	
	@Override
	@Transactional
	public UserGroupProjectDTO update(UserGroupProjectDTO toUpdate) throws EntityNotFoundException {
		
		if(userGroupProjectRepository.findOne(toUpdate.getUsergroupprojectid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public UserGroupProjectDTO findByID(int id) throws EntityNotFoundException {
		UserGroupProject ugp = userGroupProjectRepository.findOne(id);
		
		if(ugp == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(ugp, UserGroupProjectDTO.class);
	}

	@Override
	public List<UserGroupProjectDTO> findByGroup(int groupId) {
		return modelMapper.map(userGroupProjectRepository.findByGroup(groupId), 
				new TypeToken<List<UserGroupProjectDTO>>() {}.getType());
	}

	@Override
	public List<UserGroupProjectDTO> findByProject(int prjId) {
		return modelMapper.map(userGroupProjectRepository.findByProject(prjId), 
				new TypeToken<List<UserGroupProjectDTO>>() {}.getType());
	}

	@Override
	public List<UserGroupProjectDTO> findByUser(int userId) {
		return modelMapper.map(userGroupProjectRepository.findByUser(userId), 
				new TypeToken<List<UserGroupProjectDTO>>() {}.getType());
	}
	
	@Override
	public List<UserGroupProjectDTO> findByUser(String userName) {
		return modelMapper.map(userGroupProjectRepository.findByUser(userName), 
				new TypeToken<List<UserGroupProjectDTO>>() {}.getType());
	}
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails findUserDetails(String userName) {
		List<UserGroupProject> userInfo = userGroupProjectRepository.findByUser(userName);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		if(userInfo==null || userInfo.size()==0)
			return null;
		
		for(UserGroupProject ugp : userInfo)
		{
			authorities.add(new GrantedAuthorityImpl(ugp.getUsergroup().getName(),ugp.getProject().getName()));
		}
		
		UserDetails details  = new UserDetailsImpl(userInfo.get(0).getAppuser().getName(),userInfo.get(0).getAppuser().getPassword(),authorities);
		return details;
	}
	
	@Override
	public UserGroupProjectDTO findByUserAndProject(int userId, int projectId) {
		UserGroupProject ugp = userGroupProjectRepository.findByUserAndProject(userId, projectId);
		if(ugp == null)
			return null;
		return modelMapper.map(ugp, UserGroupProjectDTO.class);
	}
	
}