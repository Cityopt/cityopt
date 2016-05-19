package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ModelParameterDTO  extends BaseDTO{

	@Getter @Setter private int modelparamid;
	@Getter @Setter private InputParameterDTO inputparameter;
	@Getter @Setter private ScenarioGeneratorSimpleDTO scenariogenerator;
	@Getter @Setter private String value;
	@Getter @Setter private String expression;	
	@Getter @Setter private TimeSeriesDTO timeseries;
}
