package eu.cityopt.service;

import java.io.File;
import java.text.ParseException;
import java.time.Instant;
import java.util.Map;

import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.Type;
import eu.cityopt.opt.io.TimeSeriesData.Series;

public interface ImportService {
	
	public Map<Integer,TimeSeries> importTimeSeries(File timeSeriesInput) 
		throws EntityNotFoundException, ParseException;
	
	public void importExtParamValSet(Integer prjid, File epValSetInput, File timeSeriesInput) 
			throws EntityNotFoundException;

	void importSimulationResults(int scenid, File simResInput,
			File timeSeriesInput, int typeid) throws EntityNotFoundException,
			ParseException;

	TimeSeries saveTimeSeriesData(Series data, Type type, Instant timeOrigin);
}
