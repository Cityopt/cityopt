package com.ait.service;

import java.util.List;

import com.ait.model.Project;

public interface ProjectService {
	List<Project> findAllProjects();

	Project save(Project project);
	
	void deleteAll();
	
	Project findByID(Integer id);

}
