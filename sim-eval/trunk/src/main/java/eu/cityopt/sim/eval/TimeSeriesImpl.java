package eu.cityopt.sim.eval;

import java.util.Locale;

import org.python.core.PyObject;

/**
 * Time series representation for expression evaluation in Python.
 * In Java code, please use the TimeSeries interface.
 *
 * The Python interface in particular IS SUBJECT TO CHANGE.
 * <p>
 * Suppose ts and t2 are TimeSeries objects accessible in expressions, for
 * example a named external parameter or an output variable. Then the following
 * expressions to access the time series are supported (TODO):</p>
 * <p>
 *  <code> ts.datetimes </code><br>
 *  - List of datetime objects specifying the defined time points.
 *    See the documentation of the datetime module in the Python standard library.
 * <p>
 *  <code> ts.times </code><br>
 *  - Array of doubles specifying the defined time points as the number of
 *    seconds since 1 January 1970.  Equivalent to ts.datetimes.
 * <p>
 *  <code> ts.values </code><br>
 *  - Array of doubles containing the time series values at the defined points. 
 * <p>
 *  <code> ts.min </code><br>
 *  <code> min(ts) </code><br>
 *  - The minimum value of the time series.  (Infimum if the time series is
 *    not continuous.)
 * <p>
 *  <code> ts.max </code><br>
 *  <code> max(ts) </code><br>
 *  - The maximum value of the time series.  (Supremum if the time series is
 *    not continuous.)
 * <p>
 *  <code> ts.mean </code><br>
 *  <code> mean(ts) </code><br>
 *  - The mean of the time series as a continuous function, using interpolation
 *    between defined points.
 * <p>
 *  <code> ts.stdev </code><br>
 *  <code> stdev(ts) </code><br>
 *  - Standard deviation of the time series as a continuous function, using
 *    interpolation between defined points.
 * <p>
 *  <code> ts.var </code><br>
 *  <code> var(ts) </code><br>
 *  - Variance of the time series as a continuous function, using interpolation
 *    between defined points.
 * <p>
 *  <code> 2 * ts </code><br>
 *  - Scalar multiplication of the values.
 * <p>
 *  <code> ts + 1 </code><br>
 *  - Addition of a constant offset to the values.
 * <p>
 *  <code> ts + t2 </code><br>
 *  - Addition of two time series, considered as piecewise functions extending
 *    from the first data point to the last data point, and zero elsewhere.
 *    The result uses linear interpolation if either of the terms does.
 * <p>
 *  <code> ts * t2 </code><br>
 *  - Pointwise multiplication of two time series, evaluated at the points
 *    where either of the two time series is defined, using interpolation
 *    to fill missing values.
 *    The result uses linear interpolation if either of the factors does.
 * <p>
 *  <code> abs(ts) </code><br>
 *  - Absolute values of the time series.  Zeroes are retained at the
 *    appropriate time points by using interpolation.
 *
 * @author Hannu Rummukainen
 */
public class TimeSeriesImpl implements TimeSeries {
    private final Evaluator evaluator;
    private final PiecewiseFunction fun;

    private PyObject datetimes;

    private boolean statisticsOk;
    private double mean;
    private double var;
    private double max;
    private double min;

    TimeSeriesImpl(Evaluator evaluator, PiecewiseFunction fun) {
        this.evaluator = evaluator;
        this.fun = fun;
    }

    @Override
    public double[] getTimes() {
        return fun.tt;
    }

    @Override
    public double[] getValues() {
        return fun.vv;
    }

    @Override
    public int getDegree() {
        return fun.degree;
    }

    @Override
    public double[] valuesAt(double[] times) {
        return fun.interpolate(times);
    }

    @Override
    public PiecewiseFunction internalFunction() {
        return fun;
    }

    /** Returns a brief human-readable overview of the time series. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{ length = %d, mean = %g, stdev = %g }",
                fun.vv.length, getMean(), getStdev());
    }

    public Object getDatetimes() throws Throwable {
        if (datetimes == null) {
            datetimes = evaluator.convertTimestampsToDatetimes(getTimes());
        }
        return datetimes;
    }

    @Override
    public double getMean() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return mean;
    }

    @Override
    public double getVar() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return var;
    }

    @Override
    public double getStdev() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return Math.sqrt(var);
    }

    @Override
    public double getMax() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return max;
    }

    @Override
    public double getMin() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return min;
    }

    private void computeStatistics() {
        mean = fun.mean();
        var = fun.variance(mean);
        max = fun.sup();
        min = fun.inf();
        statisticsOk = true;
    }

    public TimeSeriesImpl __add__(double a) {
        return new TimeSeriesImpl(evaluator, fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = a + u[i];
                    }
                }));
    }

    public TimeSeriesImpl __radd__(double a) {
        return __add__(a);
    }

    public TimeSeriesImpl __add__(TimeSeriesImpl other) {
        return new TimeSeriesImpl(evaluator, fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] += u[i];
                    }
                }));
    }

    public TimeSeriesImpl __sub__(double a) {
        return __add__(-a);
    }

    public TimeSeriesImpl __rsub__(double a) {
        return new TimeSeriesImpl(evaluator, fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = a - u[i];
                    }
                }));
    }

    public TimeSeriesImpl __sub__(TimeSeriesImpl other) {
        return new TimeSeriesImpl(evaluator, fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = u[i] - v[i];
                    }
                }));
    }

    public TimeSeriesImpl __mul__(double a) {
        return new TimeSeriesImpl(evaluator, fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = a * u[i];
                    }
                }));
    }

    public TimeSeriesImpl __rmul__(double a) {
        return __mul__(a);
    }

    public TimeSeriesImpl __mul__(TimeSeriesImpl other) {
        return new TimeSeriesImpl(evaluator, fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] *= u[i];
                    }
                }));
    }

    public TimeSeriesImpl __pow__(double a) {
        return new TimeSeriesImpl(evaluator, fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = Math.pow(u[i],  a);
                    }
                }));
    }

    public TimeSeriesImpl __neg__() {
        return new TimeSeriesImpl(evaluator, fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = -u[i];
                    }
                }));
    }

    public TimeSeriesImpl __pos__() {
        return this;
    }

    public TimeSeriesImpl __abs__() {
        return new TimeSeriesImpl(evaluator, fun.abs());
    }
}
