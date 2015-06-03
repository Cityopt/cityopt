package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ScenarioMetricsDTO extends BaseDTO {

	@Getter @Setter private int scenmetricid;
	@Getter @Setter private ScenarioDTO scenario;
//	@Getter @Setter private Set<MetricVal> metricvals = new HashSet<MetricVal>(0);
//	@Getter @Setter private Set<ExtParamValScenMetric> extparamvalscenmetrics = new HashSet<ExtParamValScenMetric>(
//			0);
}
