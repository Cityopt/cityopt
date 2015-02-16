package eu.cityopt.sim.eval;

/**
 * Time series representation for expression evaluation. Instances are created
 * by calling Evaluator.makeTS, and can be put in ExternalParameters and
 * SimulationResults.
 *
 * @see Evaluator#makeTS(Type, double[], double[])
 *
 * @author Hannu Rummukainen
 */
public interface TimeSeriesI {
    /**
     * The time points where the time series is explicitly defined. Specified as
     * seconds from simulation time origin. Must be in ascending order (but
     * vertical segments are allowed in linear interpolation).
     */
    public double[] getTimes();

    /** The time series values at the defined time points. */
    public double[] getValues();

    /** Degree of interpolation: 0 for step function, 1 for piecewise linear. */
    public int getDegree();

    /** Externally provided identifier, if available. */
    Integer getTimeSeriesId();
    void setTimeSeriesId(Integer value);

    /**
     * Interpolate the time series values at the given time points.
     * @param t time points in increasing order
     */
    public double[] at(double[] t);

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

    /** Minimum value. */
    public double getMin();

    /** Maximum value. */
    public double getMax();

    /**
     * Returns a function representation of the time series.
     */
    public PiecewiseFunction internalFunction();
}
