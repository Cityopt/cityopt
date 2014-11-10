package eu.cityopt.sim.eval;

import java.util.Arrays;
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
 *  - Absolute values of the time series.  Zeroes are retained at the
 *    appropriate time points by using linear interpolation.
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
        long[] tto = merge(timeMillis, other.timeMillis);
        double[] vvo = valuesAt(tto);
        double[] vvb = other.valuesAt(tto);
        for (int i = 0; i < tto.length; ++i) {
            vvo[i] += vvb[i];
        }
        return new TimeSeriesImpl(evaluator, tto, vvo);
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

    public TimeSeriesImpl __sub__(TimeSeriesImpl other) {
        long[] tto = merge(timeMillis, other.timeMillis);
        double[] vvo = valuesAt(tto);
        double[] vvb = other.valuesAt(tto);
        for (int i = 0; i < tto.length; ++i) {
            vvo[i] -= vvb[i];
        }
        return new TimeSeriesImpl(evaluator, tto, vvo);
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

    public TimeSeriesImpl __mul__(TimeSeriesImpl other) {
        long[] tto = merge(timeMillis, other.timeMillis);
        double[] vvo = valuesAt(tto);
        double[] vvb = other.valuesAt(tto);
        for (int i = 0; i < tto.length; ++i) {
            vvo[i] *= vvb[i];
        }
        return new TimeSeriesImpl(evaluator, tto, vvo);
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
        int ni = values.length;
        if (ni == 0) {
            return this;
        }

        // Count the number of zero-crossings for which we need extra points.
        int no = ni;
        double vp = values[0];
        for (int ii = 1; ii < ni; ++ii) {
            double v = values[ii];
            if ((v < 0 && vp > 0) || (v > 0 && vp < 0)) {
                ++no;
            }
            vp = v;
        }

        // Generate the time and value vectors with zero-crossings inserted.
        long[] to = new long[no]; 
        double[] vo = new double[no];
        long tp = timeMillis[0];
        vp = values[0];
        to[0] = tp;
        vo[0] = Math.abs(vp);
        for (int ii = 1, io = 1; ii < ni; ++ii, ++io) {
            long t = timeMillis[ii];
            double v = values[ii];
            if ((v < 0 && vp > 0) || (v > 0 && vp < 0)) {
                to[io] = tp + Math.round((t - tp) * ((-vp) / (v - vp)));
                vo[io] = 0.0;
                ++io;
            }
            to[io] = t;
            vo[io] = Math.abs(v);
            tp = t;
            vp = v;
        }
        return new TimeSeriesImpl(evaluator, to, vo);
    }

    /** Merges two sorted arrays, eliminating duplicates. */
    private static long[] merge(long[] tta, long[] ttb) {
        int na = tta.length;
        int nb = ttb.length;

        // Count the time points in the result.
        int no = 0;
        int ia = 0;
        int ib = 0;
        while (ia < na && ib < nb) {
            ++no;
            long ta = tta[ia];
            long tb = ttb[ib];
            if (ta <= tb) ++ia;
            if (tb <= ta) ++ib;
        }
        no += na - ia;
        no += nb - ib;

        // Create the result by merging.
        long[] tto = new long[no];
        int io = 0;
        ia = 0;
        ib = 0;
        while (ia < na && ib < nb) {
            long ta = tta[ia];
            long tb = ttb[ib];
            if (ta < tb) {
                tto[io] = ta;
                ++io;
                ++ia;
            } else if (tb < ta) {
                tto[io] = tb;
                ++io;
                ++ib;
            } else { // ta == tb
                tto[io] = ta;
                ++io;
                ++ia;
                ++ib;
            } 
        }
        while (ia < na) {
            tto[io] = tta[ia];
            ++io;
            ++ia;
        }
        while (ib < nb) {
            tto[io] = ttb[ib];
            ++io;
            ++ib;
        }
        if (io != tto.length) {
            throw new IllegalStateException();
        }
        return tto;
    }

    public double[] valuesAt(long[] tt) {
        int no = tt.length;
        double[] vvo = new double[no];
        int na = values.length;
        if (no == 0 || na == 0) {
            return vvo;
        }
        int ia = Arrays.binarySearch(timeMillis, tt[0]);
        if (ia < 0) ia = ~ia;
        if (ia == na) {
            return vvo;
        }
        long ta = timeMillis[ia];
        for (int io = 0; io < no; ++io) {
            long to = tt[io];
            if (to > ta) {
                ++ia;
                if (ia == na) {
                    return vvo;
                }
                ta = timeMillis[ia];
                if (to > ta) {
                    ia = Arrays.binarySearch(timeMillis, ia+1, na, to);
                    if (ia < 0) ia = ~ia;
                    if (ia == na) {
                        return vvo;
                    }
                    ta = timeMillis[ia];
                }
            }
//            if (!(ta == timeMillis[ia] && to <= ta && (ia == 0 || to > timeMillis[ia-1]))) {
//                throw new IllegalStateException();
//            }
            if (to == ta) {
                vvo[io] = values[ia];
                ++ia;
                if (ia == na) {
                    return vvo;
                }
                ta = timeMillis[ia];
            } else { // to < ta
                if (ia > 0) {
                    long ta0 = timeMillis[ia-1];
                    double va0 = values[ia-1];
                    double va = values[ia];
                    double f = (to - ta0) / (double)(ta - ta0);
                    vvo[io] = va0 + f * (va - va0);
                }
            }
        }
        return vvo;
    }
}
