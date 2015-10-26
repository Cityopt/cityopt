package eu.cityopt.opt.io;

import java.util.HashMap;
import java.util.Map;

import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.TimeSeriesI;

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

        public Series(double[] times, double[] values) {
            this.times = times;
            this.values = values;
        }

        Series() {}

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

    /**
     * Reserved key for labeling the time column.
     * Do not use as a series name. 
     */
    static final String TIMESTAMP_KEY = "timestamp";

    Map<String, Series> seriesData = new HashMap<>();
    private final EvaluationSetup evaluationSetup;

    /** Returns the data for the named series, or null if not available. */
    public Series getSeries(String seriesName) {
        return seriesData.get(seriesName);
    }
    
    /** Return the underlying map.
     */
    public Map<String, TimeSeriesData.Series> getMap() {
        return seriesData;
    }
    
    public boolean isEmpty() {return seriesData.isEmpty();}

    /** Store data for a time series. */
    public void put(String name, double[] times, double[] values) {
        Series s = new Series(times, values);
        seriesData.put(name, s);
    }
    
    /** Store a time series. */
    public void put(String name, TimeSeriesI series) {
        put(name, series.getTimes(), series.getValues());
    }
    
    /** Store a time series. */
    public void put(String name, TimeSeriesData.Series series) {
        seriesData.put(name, series);
    }

    public EvaluationSetup getEvaluationSetup() {return evaluationSetup;}
}
