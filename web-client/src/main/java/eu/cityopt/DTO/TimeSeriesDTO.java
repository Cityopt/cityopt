package eu.cityopt.DTO;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class TimeSeriesDTO {

	@Getter @Setter private int tseriesid;
	@Getter @Setter private TypeDTO type;
//	@Getter @Setter private Set<TimeSeriesVal> timeseriesvals = new HashSet<TimeSeriesVal>(0);
//	@Getter @Setter private Set<ExtParam> extparams = new HashSet<ExtParam>(0);
//	@Getter @Setter private Set<MetricVal> metricvals = new HashSet<MetricVal>(0);
//	@Getter @Setter private Set<ExtParamVal> extparamvals = new HashSet<ExtParamVal>(0);

}
