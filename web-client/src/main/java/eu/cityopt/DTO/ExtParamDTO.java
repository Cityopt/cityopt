package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ExtParamDTO {
	@Getter @Setter private int extparamid;
	@Getter @Setter private UnitDTO unit;
	@Getter @Setter private TimeSeriesDTO defaulttimeseries;
//	@Getter @Setter private ProjectDTO project;
//	@Getter @Setter private int prjid;
	@Getter @Setter private String defaultvalue;
	@Getter @Setter private String name;
//	@Getter @Setter private Set<ExtParamVal> extparamvals = new HashSet<ExtParamVal>(0);

}
