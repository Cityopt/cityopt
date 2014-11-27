package com.cityopt.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.AppUser;

public interface AppUserService {

	List<AppUser> findAll();

	AppUser save(AppUser u);

	void deleteAll();

	void delete(AppUser u);

	AppUser findByID(Integer id);

	List<AppUser> findByUserName(String name);

}