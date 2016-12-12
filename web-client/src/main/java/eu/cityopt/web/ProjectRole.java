package eu.cityopt.web;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ProjectDTO;
import lombok.Getter;
import lombok.Setter;

public class ProjectRole {
	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private AppUserDTO user;
	@Getter @Setter private String projectRole;
}

