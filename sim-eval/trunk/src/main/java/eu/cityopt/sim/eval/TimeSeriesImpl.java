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
 * <p><code> ts.datetimes </code><br/>
 *  - List of datetime objects specifying the defined time points.
 *    See the documentation of the datetime module in the Python standard library.
 *</p>
 * <p><code> ts.timeMillis </code><br/>
 *  - Array of integers specifying the defined time points as the number of
 *    milliseconds since 1 January 1970.  Equivalent to ts.datetimes.
 *</p>
 * <p><code> ts.values </code><br/>
 *  - Array of doubles containing the time series values at the defined points. 
 *</p>
 * <p><code> ts.mean </code><br/>
 *    <code> mean(ts) </code><br/>
 *  - The mean of the time series as a continuous function, using linear
 *    interpolation between defined points.
 *</p>
 * <p><code> ts.stdev </code><br/>
 *    <code> stdev(ts) </code><br/>
 *  - Standard deviation of the time series as a continuous function, using
 *    linear interpolation between defined points.
 *</p>
 * <p><code> ts.var </code><br/>
 *    <code> var(ts) </code><br/>
 *  - Variance of the time series as a continuous function, using linear
 *    interpolation between defined points.
 *</p>
 * <p><code> 2 * ts </code><br/>
 *  - Scalar multiplication of the values.
 *</p>
 * <p><code> ts + 1 </code><br/>
 *  - Addition of a constant offset to the values.
 *</p>
 * <p><code> ts + t2 </code><br/>
 *  - Addition of two time series, considered as piecewise linear functions
 *    extending from the first data point to the last data point, and zero
 *    elsewehere.
 *</p>
 * <p><code> ts * t2 </code><br/>
 *  - Pointwise multiplication of two time series, evaluated at the points
 *    where either of the two time series is defined, using linear
 *    interpolation to fill missing values.
 *</p>
 * <p><code> abs(ts) </code><br/>
 *  - Absolute values of the time series values.
 *    TODO: linear interpolation vs. zero-crossings
 *</p>
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class TimeSeriesImpl implements TimeSeries {
    private final Evaluator evaluator;

    private final long[] timeMillis;
    private final double[] values;

    private Object datetimes;

    private boolean statisticsOk;
    private double mean;
    private double var;

    TimeSeriesImpl(Evaluator evaluator, long[] timeMillis, double[] values) {
        this.evaluator = evaluator;
        this.timeMillis = timeMillis;
        this.values = values;
    }

    /**
     * Constructs a time series using the same time points as another instance,
     * and different values.  The instance is connected to the same evaluator.
     */
    TimeSeriesImpl(TimeSeriesImpl other, double[] values) {
        this.evaluator = other.evaluator;
        this.timeMillis = other.timeMillis;
        this.values = values;
        this.datetimes = other.datetimes;
    }

    public long[] getTimeMillis() {
        return timeMillis;
    }

    public double[] getValues() {
        return values;
    }

    /** Returns a brief human-readable overview of the time series. */
    public String toString() {
        return String.format(Locale.ROOT, "{ length = %d, mean = %g, stdev = %g }",
                values.length, getMean(), getStdev());
    }

    public Object getDatetimes() throws Throwable {
        if (datetimes == null) {
            datetimes = evaluator.invokeInternal(
                    "convertTimeMillisToDatetimes", new Object[] { timeMillis });
        }
        return datetimes;
    }

    public double getMean() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return mean;
    }

    public double getVar() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return var;
    }

    public double getStdev() {
        if (!statisticsOk) {
            computeStatistics();
        }
        return Math.sqrt(var);
    }

    private void computeStatistics() {
        // We assume that the values can be interpolated linearly between time points.
        int n = values.length;
        if (n == 0) {
            mean = 0.0;
            var = 0.0;
        } else if (n == 1) {
            mean = values[0];
            var = 0.0;
        } else {
            {
                long t = timeMillis[1];
                long dt = t - timeMillis[0];
                double v0 = values[0];
                double v = values[1];
                double vs = dt * v0;
                for (int i = 2; i < n; ++i) {
                    long t1 = timeMillis[i];
                    long dt1 = t1 - t;
                    double v1 = values[i];
                    vs += (dt + dt1) * v;
                    t = t1;
                    dt = dt1;
                    v = v1;
                }
                vs += dt * v;
                double ts = t - timeMillis[0];
                mean = vs / (2.0 * ts);
            }
            {
                long t = timeMillis[1];
                long dt = t - timeMillis[0];
                double v0 = values[0] - mean;
                double v = values[1] - mean;
                double vss = dt * v0 * (v0 + v);
                for (int i = 2; i < n; ++i) {
                    long t1 = timeMillis[i];
                    long dt1 = t1 - t;
                    double v1 = values[i] - mean;
                    vss += v * ((dt + dt1) * v + dt1 * v1);
                    t = t1;
                    dt = dt1;
                    v = v1;
                }
                vss += dt * v * v;
                double ts = t - timeMillis[0];
                var = vss / (3.0 * ts);
            }
        }
        statisticsOk = true;
    }

    public TimeSeriesImpl __add__(double a) {
        int n = values.length;
        double[] v = new double[n];
        for (int i = 0; i < n; ++i) {
            v[i] = a + values[i];
        }
        return new TimeSeriesImpl(this, v);
    }

    public TimeSeriesImpl __radd__(double a) {
        return __add__(a);
    }

    public TimeSeriesImpl __add__(TimeSeriesImpl other) {
        int n = values.length;
        double[] v = new double[n];
        for (int i = 0; i < n; ++i) {
            //TODO merge by timeMillis
            v[i] = values[i] + other.values[i];
        }
        return new TimeSeriesImpl(this, v);
    }

    public TimeSeriesImpl __sub__(double a) {
        return __add__(-a);
    }

    public TimeSeriesImpl __rsub__(double a) {
        int n = values.length;
        double[] v = new double[n];
        for (int i = 0; i < n; ++i) {
            v[i] = a - values[i];
        }
        return new TimeSeriesImpl(this, v);
    }

    public TimeSeriesImpl __mul__(double a) {
        int n = values.length;
        double[] v = new double[n];
        for (int i = 0; i < n; ++i) {
            v[i] = a * values[i];
        }
        return new TimeSeriesImpl(this, v);
    }

    public TimeSeriesImpl __rmul__(double a) {
        return __mul__(a);
    }

    public TimeSeriesImpl __pow__(double a) {
        int n = values.length;
        double[] v = new double[n];
        for (int i = 0; i < n; ++i) {
            v[i] = Math.pow(values[i], a);
        }
        return new TimeSeriesImpl(this, v);
    }

    public TimeSeriesImpl __neg__() {
        int n = values.length;
        double[] v = new double[n];
        for (int i = 0; i < n; ++i) {
            v[i] = -values[i];
        }
        return new TimeSeriesImpl(this, v);
    }

    public TimeSeriesImpl __pos__() {
        return this;
    }

    public TimeSeriesImpl __abs__() {
        //TODO handle zero-crossings in time series using linear interpolation
        int n = values.length;
        double[] v = new double[n];
        for (int i = 0; i < n; ++i) {
            v[i] = Math.abs(values[i]);
        }
        return new TimeSeriesImpl(this, v);
    }
}
