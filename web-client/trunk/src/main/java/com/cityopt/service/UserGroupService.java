package com.cityopt.service;

import java.util.List;

import com.cityopt.model.UserGroup;

public interface UserGroupService {

	List<UserGroup> findAll();

	List<UserGroup> findByGroupName(String userGroupName);

	UserGroup save(UserGroup model);

	void deleteAll();

	void delete(UserGroup userGroup);

}