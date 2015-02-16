package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class SearchConstraintDTO {

	@Getter @Setter private int scid;
//	@Getter @Setter private Unit unit;
//	@Getter @Setter private Project project;
	@Getter @Setter private String expression;
	@Getter @Setter private Double lowerbound;
	@Getter @Setter private Double upperbound;
//	@Getter @Setter private Set<OptSearchConst> optsearchconsts = new HashSet<OptSearchConst>(0);
}
