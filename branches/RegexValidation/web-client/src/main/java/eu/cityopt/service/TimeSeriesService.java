package eu.cityopt.service;

import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.model.TimeSeries;

public interface TimeSeriesService extends CityOptService<TimeSeries> {

	TimeSeries save(TimeSeriesDTOX ts);

}