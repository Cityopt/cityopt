package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class OptConstraintDTO {

	@Getter @Setter private int optconstid;
	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private String name;
	@Getter @Setter private String expression;
	@Getter @Setter private String lowerbound;
	@Getter @Setter private String upperbound;
//	@Getter @Setter private Set<ScenGenOptConstraint> scengenoptconstraints = new HashSet<ScenGenOptConstraint>(
//			0);
}
