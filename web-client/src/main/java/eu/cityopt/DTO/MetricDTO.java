package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class MetricDTO {
	@Getter @Setter private int metid;
//	@Getter @Setter private Unit unit;
//	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private String name;
	@Getter @Setter private String expression;
//	@Getter @Setter private Set<MetricVal> metricvals = new HashSet<MetricVal>(0);

}
