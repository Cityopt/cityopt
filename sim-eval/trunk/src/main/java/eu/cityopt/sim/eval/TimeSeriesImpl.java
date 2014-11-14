package eu.cityopt.sim.eval;

import java.util.Locale;

/**
 * Time series representation for expression evaluation in Python.
 * In Java code, please use the TimeSeries interface.
 *
 * The Python interface in particular IS SUBJECT TO CHANGE.
 *
 * <p>Suppose ts and t2 are TimeSeries objects accessible in expressions, for
 * example a named external parameter or an output variable. Then the following
 * expressions to access the time series are supported (TODO):</p>
 *
 * <p><code> ts.datetimes </code><br>
 *  - List of datetime objects specifying the defined time points.
 *    See the documentation of the datetime module in the Python standard library.
 *</p>
 * <p><code> ts.timeMillis </code><br>
 *  - Array of integers specifying the defined time points as the number of
 *    milliseconds since 1 January 1970.  Equivalent to ts.datetimes.
 *</p>
 * <p><code> ts.values </code><br>
 *  - Array of doubles containing the time series values at the defined points. 
 *</p>
 * <p><code> ts.mean </code><br>
 *    <code> mean(ts) </code><br>
 *  - The mean of the time series as a continuous function, using interpolation
 *    between defined points.
 *</p>
 * <p><code> ts.stdev </code><br>
 *    <code> stdev(ts) </code><br>
 *  - Standard deviation of the time series as a continuous function, using
 *    interpolation between defined points.
 *</p>
 * <p><code> ts.var </code><br>
 *    <code> var(ts) </code><br>
 *  - Variance of the time series as a continuous function, using interpolation
 *    between defined points.
 *</p>
 * <p><code> 2 * ts </code><br>
 *  - Scalar multiplication of the values.
 *</p>
 * <p><code> ts + 1 </code><br>
 *  - Addition of a constant offset to the values.
 *</p>
 * <p><code> ts + t2 </code><br>
 *  - Addition of two time series, considered as piecewise functions extending
 *    from the first data point to the last data point, and zero elsewhere.
 *    The result uses linear interpolation if either of the terms does.
 *</p>
 * <p><code> ts * t2 </code><br>
 *  - Pointwise multiplication of two time series, evaluated at the points
 *    where either of the two time series is defined, using interpolation
 *    to fill missing values.
 *    The result uses linear interpolation if either of the factors does.
 *</p>
 * <p><code> abs(ts) </code><br>
 *  - Absolute values of the time series.  Zeroes are retained at the
 *    appropriate time points by using interpolation.
 *</p>
 * @author Hannu Rummukainen
 */
public class TimeSeriesImpl implements TimeSeries {
    private final Evaluator evaluator;
    private final PiecewiseFunction fun;

    private Object datetimes;

    private boolean statisticsOk;
    private double mean;
    private double var;

    TimeSeriesImpl(Evaluator evaluator, PiecewiseFunction fun) {
        this.evaluator = evaluator;
        this.fun = fun;
    }

    @Override
    public long[] getTimeMillis() {
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
    public double[] valuesAt(long[] times) {
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
            datetimes = evaluator.invokeInternal(
                    "convertTimeMillisToDatetimes",
                    new Object[] { getTimeMillis() });
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

    private void computeStatistics() {
        mean = fun.mean();
        var = fun.variance(mean);
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
