package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class DecisionVariableDTO  extends BaseDTO{

	@Getter @Setter private int decisionvarid;
	@Getter @Setter private TypeDTO type;
	@Getter @Setter private ScenarioGeneratorSimpleDTO scenariogenerator;
	@Getter @Setter private InputParameterDTO inputparameter;
	@Getter @Setter private String name;
	@Getter @Setter private String lowerbound;
	@Getter @Setter private String upperbound;

}
