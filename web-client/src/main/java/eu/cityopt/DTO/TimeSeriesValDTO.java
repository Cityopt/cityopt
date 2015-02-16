package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class TimeSeriesValDTO {

	@Getter @Setter private int tseriesvalid;
//	@Getter @Setter private TimeSeries timeseries;
	@Getter @Setter private String value;
	@Getter @Setter private Date time;

}
