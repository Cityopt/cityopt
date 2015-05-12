package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ExtParamDTO {
	@Getter @Setter private int extparamid;
	@Getter @Setter private UnitDTO unit;
//	@Getter @Setter private TypeDTO type;
//	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private String name;
//	@Getter @Setter private Set<ExtParamVal> extparamvals = new HashSet<ExtParamVal>(0);

}
