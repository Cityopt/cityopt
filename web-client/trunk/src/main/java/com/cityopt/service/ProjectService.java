package com.cityopt.service;

import com.cityopt.model.Project;

public interface ProjectService extends CityOptService<Project> {

	void deleteAll();
}
