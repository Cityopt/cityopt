package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ExtParamValDTO implements java.io.Serializable {

	@Getter @Setter private int extparamvalid;
	@Getter @Setter private TimeSeriesDTO timeseries;
	@Getter @Setter private ExtParamDTO extparam;
	@Getter @Setter private String value;
	@Getter @Setter private String comment;
//	@Getter @Setter private Set<ExtParamValScenMetric> extparamvalscenmetrics = new HashSet<ExtParamValScenMetric>(
//			0);
//	@Getter @Setter private Set<ExtParamValScenGen> extparamvalscengens = new HashSet<ExtParamValScenGen>(
//			0);
}
