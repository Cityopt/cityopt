package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.model.Type;

public class UnitDTO {
	@Getter @Setter private int unitid;
	@Getter @Setter private Type type;
	@Getter @Setter private String name;
//	@Getter @Setter private Set<ExtParam> extparams = new HashSet<ExtParam>(0);
//	@Getter @Setter private Set<SearchConstraint> searchconstraints = new HashSet<SearchConstraint>(
//			0);
//	@Getter @Setter private Set<InputParameter> inputparameters = new HashSet<InputParameter>(0);
//	@Getter @Setter private Set<Metric> metrics = new HashSet<Metric>(0);

}
