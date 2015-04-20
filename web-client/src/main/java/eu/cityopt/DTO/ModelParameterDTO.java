package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ModelParameterDTO {

	@Getter @Setter private int modelparamid;
	@Getter @Setter private InputParameterDTO inputparameter;
//	@Getter @Setter private ScenarioGenerator scenariogenerator;
	@Getter @Setter private String value;
	@Getter @Setter private String expression;
}
