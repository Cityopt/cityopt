package com.cityopt.service;

import java.util.List;

import com.cityopt.model.UserGroupProject;

public interface UserGroupProjectService {

	List<UserGroupProject> findAll();

	UserGroupProject save(UserGroupProject u);

	void delete(UserGroupProject u);

	UserGroupProject findByID(Integer id);

}