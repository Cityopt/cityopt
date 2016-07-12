package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class TimeSeriesDTOX extends TimeSeriesDTO {
	@Getter @Setter private Date[] times;
	@Getter @Setter private double[] values;
}
