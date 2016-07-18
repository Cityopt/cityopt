package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.model.AppUser;
import eu.cityopt.model.Project;
import eu.cityopt.model.UserGroup;
import eu.cityopt.model.UserGroupProject;
import eu.cityopt.repository.AppUserRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.UserGroupProjectRepository;
import eu.cityopt.repository.UserGroupRepository;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.EntityNotFoundException;

@Service("AppUserService")
public class AppUserServiceImpl implements AppUserService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AppUserRepository appuserRepository;
	
	@Autowired 
	private UserGroupProjectRepository userGroupProjectRepository;
	
	@Autowired 
	private UserGroupRepository userGroupRepository;
	
	@Autowired 
	private ProjectRepository projectRepository;
	
	@Transactional(readOnly = true)
	@Override
	public List<AppUserDTO> findAll() {
		return modelMapper.map(appuserRepository.findAll(), 
				new TypeToken<List<AppUserDTO>>() {}.getType());
	}

	@Transactional
	@Override
	public AppUserDTO save(AppUserDTO u) {
		AppUser user = modelMapper.map(u, AppUser.class);
		user = appuserRepository.save(user);
		return modelMapper.map(user, AppUserDTO.class);
	}

	@Transactional
	public void deleteAll() {
		appuserRepository.deleteAll();
	}
	
	@Transactional
	@Override
	public void delete(int id) throws EntityNotFoundException {
		
		if(appuserRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		appuserRepository.delete(id);
	}
	
	@Transactional
	@Override
	public AppUserDTO update(AppUserDTO toUpdate) throws EntityNotFoundException {
		
		if(appuserRepository.findOne(toUpdate.getUserid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Transactional(readOnly = true)
	@Override
	public AppUserDTO findByID(int id) throws EntityNotFoundException {
		if(appuserRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(appuserRepository.findOne(id), AppUserDTO.class);
	}	
	
	@Transactional(readOnly = true)
	@Override
	public AppUserDTO findByName(String userName) {
		if(appuserRepository.findByName(userName) == null) {
			return null;
		}
		
		return modelMapper.map(appuserRepository.findByName(userName), AppUserDTO.class);
	}	
	
	@Transactional(readOnly = true)
	@Override
	public AppUserDTO findByNameAndPassword(String name, String password) throws EntityNotFoundException {
		if(appuserRepository.authenticateUser(name, password) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(appuserRepository.authenticateUser(name, password), AppUserDTO.class);
	}	
	
	@Transactional
	@Override
	public void addToUserGroupProject(int userId, int groupId, int projectId) throws EntityNotFoundException {
		AppUser user = appuserRepository.findOne(userId);
		UserGroup group = userGroupRepository.findOne(groupId);
		Project project = projectRepository.findOne(projectId);
		
		if(user == null) {
			throw new EntityNotFoundException("user with id: "
					+ userId + " not found");
		}
		if(group == null) {
			throw new EntityNotFoundException("group with id: "
					+ groupId + " not found");
		}
		if(project == null) {
			throw new EntityNotFoundException("project with id: "
					+ projectId + " not found");
		}
		
		UserGroupProject ugp = new UserGroupProject();
		ugp.setAppuser(user);
		ugp.setProject(project);
		ugp.setUsergroup(group);		
		ugp = userGroupProjectRepository.save(ugp);
		
	}
	
}
