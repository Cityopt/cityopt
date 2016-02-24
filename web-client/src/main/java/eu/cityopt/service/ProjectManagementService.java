package eu.cityopt.service;

import eu.cityopt.DTO.ProjectDTO;

public interface ProjectManagementService {

	
	public ProjectDTO createProjectWithAdminUser(ProjectDTO NewProject, String user);	
}
