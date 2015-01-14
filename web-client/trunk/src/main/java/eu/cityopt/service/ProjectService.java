package eu.cityopt.service;

import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.model.Project;

public interface ProjectService extends CityOptService<Project> {

	ProjectDTO save(ProjectDTO projectDTO);
	
	void deleteAll();
}
