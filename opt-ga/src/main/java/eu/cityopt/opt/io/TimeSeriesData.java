package eu.cityopt.opt.io;

import java.util.HashMap;
import java.util.Map;

import eu.cityopt.sim.eval.EvaluationSetup;

/**
 * Access to time series data by time series name.
 * Covers only the time and value coordinates, ignoring the time series type.
 *
 * @author Hannu Rummukainen
 */
public class TimeSeriesData {
    public static class Series {
        double[] times;
        double[] values;
        
        public double[] getTimes(){
			return times;
		}
        public double[] getValues(){
        	return values;
        }
    }

    public TimeSeriesData(EvaluationSetup evaluationSetup) {
        this.evaluationSetup = evaluationSetup;
    }

    static final String TIMESTAMP_KEY = "timestamp";

    Map<String, Series> seriesData = new HashMap<>();
    private final EvaluationSetup evaluationSetup;

    /** Returns the data for the named series, or null if not available. */
    public Series getSeries(String seriesName) {
        return seriesData.get(seriesName);
    }

    public EvaluationSetup getEvaluationSetup() {return evaluationSetup;}
}
