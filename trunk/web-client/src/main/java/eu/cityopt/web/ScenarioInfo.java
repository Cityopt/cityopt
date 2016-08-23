package eu.cityopt.web;

import lombok.Getter;
import lombok.Setter;

public class ScenarioInfo {
	@Getter @Setter private String name;
	@Getter @Setter private int id;
	@Getter @Setter private boolean pareto;
}
