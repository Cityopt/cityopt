package eu.cityopt.web;

import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.UserGroupDTO;

public class RoleForm {
	
	@Getter @Setter private UserGroupDTO role;
	@Getter @Setter private ProjectDTO   project;
	
}
