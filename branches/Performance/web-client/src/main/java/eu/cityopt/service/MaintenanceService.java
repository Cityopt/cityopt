package eu.cityopt.service;

import java.io.File;
import java.text.ParseException;
import java.time.Instant;
import java.util.Map;

import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.Type;
import eu.cityopt.sim.eval.TimeSeriesData.Series;

public interface MaintenanceService {
	
	public void cleanupEntities();
	
}
