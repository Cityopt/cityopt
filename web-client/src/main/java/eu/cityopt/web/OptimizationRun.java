package eu.cityopt.web;

import lombok.Getter;
import lombok.Setter;

public class OptimizationRun {
	
	@Getter @Setter private int id;
	@Getter @Setter private String started;
    @Getter @Setter private String estimated;
	@Getter @Setter private String deadline;
	@Getter @Setter private String status;
}
