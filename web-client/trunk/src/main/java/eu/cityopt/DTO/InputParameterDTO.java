package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class InputParameterDTO {
	@Getter @Setter private int inputid;
	@Getter @Setter private UnitDTO unit;
	@Getter @Setter private ComponentDTO component;
	@Getter @Setter private String name;
	@Getter @Setter private String defaultvalue;
//	@Getter @Setter private Set<ModelParameter> modelparameters = new HashSet<ModelParameter>(0);
//	@Getter @Setter private Set<InputParamVal> inputparamvals = new HashSet<InputParamVal>(0);

}
