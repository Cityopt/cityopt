package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class TimeSeriesValDTO extends BaseDTO {

	@Getter @Setter private int tseriesvalid;
	@Getter @Setter private TimeSeriesDTO timeseries;
	@Getter @Setter private String value;
	@Getter @Setter private Date time;

}
