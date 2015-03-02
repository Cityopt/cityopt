package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class MetricValDTO {

	@Getter @Setter private int metricvalid;
//	@Getter @Setter private MetricDTO metric;
//	@Getter @Setter private ScenarioMetricsDTO scenariometrics;
	@Getter @Setter private TimeSeriesDTO timeseries;
	@Getter @Setter private String value;

}
