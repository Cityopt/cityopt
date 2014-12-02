package com.cityopt.service;

import java.util.List;

import com.cityopt.model.AppUser;

public interface AppUserService extends CityOptService<AppUser>{

	void deleteAll();

	List<AppUser> findByUserName(String name);
	
	AppUser authenticateUser(String name, String password);

}