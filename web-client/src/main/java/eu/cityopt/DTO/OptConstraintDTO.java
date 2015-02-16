package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class OptConstraintDTO {

	@Getter @Setter private int optconstid;
//	@Getter @Setter private Project project;
	@Getter @Setter private String expression;
	@Getter @Setter private Double lowerbound;
	@Getter @Setter private Double upperbound;
//	@Getter @Setter private Set<ScenGenOptConstraint> scengenoptconstraints = new HashSet<ScenGenOptConstraint>(
//			0);
}
