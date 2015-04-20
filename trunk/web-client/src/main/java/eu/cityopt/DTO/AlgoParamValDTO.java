package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class AlgoParamValDTO {

	@Getter @Setter private int aparamvalid;
//	private ScenarioGenerator scenariogenerator;
	@Getter @Setter private AlgoParamDTO algoparam;
	@Getter @Setter private String value;

}
