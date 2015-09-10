package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class AlgoParamValDTO extends BaseDTO{

	@Getter @Setter private int aparamvalid;
	@Getter @Setter private ScenarioGeneratorDTO scenariogenerator;
	@Getter @Setter private AlgoParamDTO algoparam;
	@Getter @Setter private String value;

}
