package eu.cityopt.web;

import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.UserGroupDTO;

public class OptimizationRun {
	
	@Getter @Setter private int id;
	@Getter @Setter private String started;
	@Getter @Setter private String deadline;
	@Getter @Setter private String status;
}
