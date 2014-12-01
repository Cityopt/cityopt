package com.cityopt.controller;

import java.sql.Date;

public class UserForm {
	private String name;
	private String company;
	private String userName;
	private String password;
	private String email;
	private String userRole;
	private String startRights;
	private String finishRights;
	private String project;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getStartRights() {
		return startRights;
	}

	public void setStartRights(String startRights) {
		this.startRights = startRights;
	}

	public String getFinishRights() {
		return finishRights;
	}

	public void setFinishRights(String finishRights) {
		this.finishRights = finishRights;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
}
