package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ExtParamValScenMetricDTO {

	@Getter @Setter private int id;
	@Getter @Setter private ExtParamValDTO extparamval;
	@Getter @Setter private ScenarioMetricsDTO scenariometrics;

}
