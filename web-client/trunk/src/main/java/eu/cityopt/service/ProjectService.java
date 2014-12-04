package eu.cityopt.service;

import eu.cityopt.model.Project;

public interface ProjectService extends CityOptService<Project> {

	void deleteAll();
}
