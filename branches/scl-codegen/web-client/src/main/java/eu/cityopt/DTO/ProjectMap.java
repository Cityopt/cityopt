package eu.cityopt.DTO;

import org.modelmapper.PropertyMap;

import eu.cityopt.model.Project;

public class ProjectMap extends PropertyMap<Project, ProjectDTO>{	
	@Override
	protected void configure() {
		map().setName(source.getName());
		skip().setProjectCreator(null);
	}
}