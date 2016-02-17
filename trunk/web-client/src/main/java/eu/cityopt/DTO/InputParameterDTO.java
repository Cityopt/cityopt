package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class InputParameterDTO  extends BaseDTO{
	@Getter @Setter private int inputid;
	@Getter @Setter private UnitDTO unit;
	@Getter @Setter private TypeDTO type;
//	@Getter @Setter private ComponentDTO component;
	@Getter @Setter private int componentComponentid;
	@Getter @Setter private String componentName;
	@Getter @Setter private String name;
	@Getter @Setter private String defaultvalue;
	@Getter @Setter private TimeSeriesDTO timeseries;
//	@Getter @Setter private Set<ModelParameter> modelparameters = new HashSet<ModelParameter>(0);
//	@Getter @Setter private Set<InputParamVal> inputparamvals = new HashSet<InputParamVal>(0);
	/// Name as used in expressions, i.e. componentName + "." + name
	public String getQualifiedName() {
		return componentName + "." + name;
	} 
}
