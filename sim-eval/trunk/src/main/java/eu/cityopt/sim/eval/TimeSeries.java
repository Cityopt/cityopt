package eu.cityopt.sim.eval;

/**
 * Time series representation for expression evaluation. TimeSeries instances
 * are created by calling Evaluator.makeTimeSeries, and can be put in
 * ExternalParameters and SimulationResults.
 *
 * @see Evaluator#makeTimeSeries(long[], double[])
 * @see Evaluator#makeTimeSeries(int)
 *
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public interface TimeSeries {
    /**
     * The time points where the time series is explicitly defined. Specified as
     * milliseconds since 1 January 1970, just like System.currentTimeMillis().
     * Must be in strictly ascending order.
     */
    public long[] getTimeMillis();

    /** The time series values at the defined time points. */
    public double[] getValues();
}
