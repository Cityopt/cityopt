package com.ait.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.model.Project;
import com.ait.repository.ProjectRepository;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService{

	@Autowired
	private ProjectRepository projectRepository;
	
	public ProjectRepository getProjectRepository() {
		return projectRepository;
	}

	public void setProjectRepository(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	public List<Project> findAllProjects() {
		return projectRepository.findAll();
	}

	@Transactional
	public Project save(Project project) {
		return projectRepository.save(project);
	}

	@Transactional
	public void deleteAll() {
		projectRepository.deleteAll();
		
	}
	
	public Project findByID(Integer id) {
		return projectRepository.findOne(id);
	}
	
	

}
