package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.model.UserGroupProject;

public interface UserGroupProjectService extends CityOptService<UserGroupProjectDTO>{

	UserGroupProjectDTO update(UserGroupProjectDTO toUpdate)
			throws EntityNotFoundException;

	UserGroupProjectDTO save(UserGroupProjectDTO u);
	
	List<UserGroupProjectDTO> findByGroup(int groupId);
	
	List<UserGroupProjectDTO> findByProject(int prjId);
	
	List<UserGroupProjectDTO> findByUser(int userId);
	
}