package com.cityopt.service;

import java.util.List;

import com.cityopt.model.Project;

public interface ProjectService {
	List<Project> findAllProjects();

	Project save(Project project);
	
	void deleteAll();
	
	void deleteProject(Project p);
	
	Project findByID(Integer id);

}
