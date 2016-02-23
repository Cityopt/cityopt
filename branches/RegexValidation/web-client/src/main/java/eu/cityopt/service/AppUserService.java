package eu.cityopt.service;

import java.util.List;

import eu.cityopt.DTO.AppUserDTO;

public interface AppUserService extends CityOptService<AppUserDTO>{
	AppUserDTO save(AppUserDTO u);
	
	AppUserDTO update(AppUserDTO toUpdate)  throws EntityNotFoundException;

	void addToUserGroupProject(int userId, int groupId, int projectId) throws EntityNotFoundException;
	
	AppUserDTO findByNameAndPassword(String name, String password) throws EntityNotFoundException;

	AppUserDTO findByName(String userName) throws EntityNotFoundException;
	
	//ToDo: findByName(String name);
	//Required because we need to find a single userDTO object 
	//from AppUserTable to make Project based authorization.
	
}