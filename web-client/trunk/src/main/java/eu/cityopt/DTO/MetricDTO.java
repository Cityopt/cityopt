package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.model.Project;
import eu.cityopt.model.Unit;

public class MetricDTO {
	@Getter @Setter private int metid;
//	@Getter @Setter private Unit unit;
//	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private String name;
	@Getter @Setter private String expression;
//	@Getter @Setter private Set<MetricVal> metricvals = new HashSet<MetricVal>(0);

}
