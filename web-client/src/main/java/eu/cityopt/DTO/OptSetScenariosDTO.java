package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class OptSetScenariosDTO {

	@Getter @Setter private int optscenid;
	@Getter @Setter private OptimizationSetDTO optimizationset;
	@Getter @Setter private ScenarioDTO scenario;
	@Getter @Setter private String value;

}
