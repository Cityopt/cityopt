package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class DecisionVariableDTO {

	@Getter @Setter private int decisionvarid;
	@Getter @Setter private TypeDTO type;
	@Getter @Setter private ScenarioGeneratorDTO scenariogenerator;
	@Getter @Setter private String name;
	@Getter @Setter private String expression;
	@Getter @Setter private String lowerbound;
	@Getter @Setter private String upperbound;

}
