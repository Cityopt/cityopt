package eu.cityopt.web;

import lombok.Getter;
import lombok.Setter;

public class ScenarioForm {
	@Getter @Setter private String name;
	@Getter @Setter private int id;
	@Getter @Setter private String description;
	@Getter @Setter private String status;
}
