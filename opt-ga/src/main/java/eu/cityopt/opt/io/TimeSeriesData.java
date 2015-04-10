package eu.cityopt.opt.io;

import eu.cityopt.sim.eval.EvaluationSetup;

/**
 * Access to time series data by time series name.
 * Covers only the time and value coordinates, ignoring the time series type.
 *
 * @author Hannu Rummukainen
 */
public interface TimeSeriesData {
    public static class Series {
        double[] times;
        double[] values;
    }

    /** Returns the data for the named series, or null if not available. */
    public Series getSeriesData(String seriesName);

    public EvaluationSetup getEvaluationSetup();
}
