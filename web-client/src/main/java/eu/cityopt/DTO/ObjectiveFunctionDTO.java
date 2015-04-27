package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class ObjectiveFunctionDTO {

	@Getter @Setter private int obtfunctionid;
	@Getter @Setter private TypeDTO type;
	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private String name;
	@Getter @Setter private String expression;
	@Getter @Setter private Boolean ismaximise;
	@Getter @Setter private Date executedat;
//	@Getter @Setter private Set<OptimizationSet> optimizationsets = new HashSet<OptimizationSet>(
//			0);
//	@Getter @Setter private Set<ScenGenObjectiveFunction> scengenobjectivefunctions = new HashSet<ScenGenObjectiveFunction>(
//			0);
}
