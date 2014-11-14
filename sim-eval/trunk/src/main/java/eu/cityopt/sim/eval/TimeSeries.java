package eu.cityopt.sim.eval;

/**
 * Time series representation for expression evaluation. TimeSeries instances
 * are created by calling Evaluator.makeTS, and can be put in ExternalParameters
 * and SimulationResults.
 *
 * @see Evaluator#makeTS(Type, double[], double[])
 *
 * @author Hannu Rummukainen
 */
public interface TimeSeries {
    /**
     * The time points where the time series is explicitly defined. Specified as
     * seconds since 1 January 1970 UTC. Must be in ascending order (but
     * vertical segments are allowed in linear interpolation).
     */
    public double[] getTimes();

    /** The time series values at the defined time points. */
    public double[] getValues();

    /** Degree of interpolation: 0 for step function, 1 for piecewise linear. */
    public int getDegree();

    /**
     * Interpolate the time series values at the given time points.
     * @param t time points in increasing order
     */
    public double[] valuesAt(double[] t);

    /**
     * The mean of the time series as a continuous function, using interpolation
     * between defined points.
     */
    public double getMean();

    /**
     * Standard deviation of the time series as a continuous function, using
     * interpolation between defined points.
     */
    public double getStdev();

    /**
     * Variance of the time series as a continuous function, using interpolation
     * between defined points.
     */
    public double getVar();

    /**
     * Returns a function representation of the time series.
     */
    public PiecewiseFunction internalFunction();
}
