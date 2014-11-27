package eu.cityopt.sim.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;

/**
 * Time series representation for expression evaluation in Python.
 * In Java code, please use the TimeSeriesI interface.
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
 *  <code> ts.at(times) </code><br>
 *  <code> ts.at(datetimes) </code><br>
 *  - Returns an array of double with interpolated time series values at the
 *    given time points.
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
 * <p>
 *  <code> integrate(ts, a, b[, scale]) </code><br>
 *  - Integral from a to b, with the time unit 'scale' (default 1 second).
 *    a and b can be timestamps or datetime objects.
 * <p>
 *  <code> ts.iter() </code><br>
 *  - Iterator over (time, value) pairs.
 *
 * @author Hannu Rummukainen
 */
public class TimeSeries implements TimeSeriesI {
    private final PiecewiseFunction fun;

    private PyObject datetimes;

    private boolean statisticsOk;
    private double mean;
    private double var;
    private double max;
    private double min;

    TimeSeries(PiecewiseFunction function) {
        this.fun = function;
    }

    /**
     * Constructs a time series representation for use in expressions.
     *
     * @param degree
     *            0 for piecewise constant, 1 for piecewise linear interpolation.
     * @param times
     *            the defined time points, seconds since 1 January 1970 UTC.
     *            Must be in ascending order (but non-consecutive vertical
     *            segments are allowed in linear interpolation). Outside the
     *            closed interval from the first to the last time point, values
     *            are assumed to be zero. Usually there should be at least two
     *            points, so that the series covers a non-empty time interval.
     * @param values
     *            the values at the defined time points. It is recommended to
     *            set the last value of a step function as 0.
     */
    public TimeSeries(int degree, double[] times, double[] values) {
        this.fun = PiecewiseFunction.make(degree, times, values);
    }

    /** Constructor for use from Python code */
    public TimeSeries(int degree, PyObject datetimes, double[] values) {
        double[] times = Evaluator.getActiveEvaluator().convertToTimestamps(datetimes);
        this.fun = PiecewiseFunction.make(degree, times, values);
    }

    private static class Pair {
        double time;
        double value;
    }

    /** Constructor for use from Python code */
    public TimeSeries(int degree, Iterable<Object> iterable) {
        Evaluator evaluator = Evaluator.getActiveEvaluator();
        List<Pair> pairs = new ArrayList<Pair>();
        for (Object o : iterable) {
            PyObject p = (PyObject) o;
            if (p.__len__() != 2) {
                throw Py.IndexError("Element is not a pair: " + p.__repr__());
            }
            PyObject first = p.__getitem__(0);
            PyObject second = p.__getitem__(1);

            Pair pair = new Pair();
            try {
                pair.time = first.asDouble();
            } catch (PyException e) {
                pair.time = evaluator.convertToTimestamp(first);
            }
            pair.value = second.asDouble();
            pairs.add(pair);
        }
        int n = pairs.size();
        double[] times = new double[n];
        double[] values = new double[n];
        for (int i = 0; i < n; ++i) {
            Pair pair = pairs.get(i);
            times[i] = pair.time;
            values[i] = pair.value;
        }
        this.fun = PiecewiseFunction.make(degree, times, values);
    }

    public static TimeSeries step(Iterable<Object> iterable) {
        return new TimeSeries(0, iterable);
    }

    public static TimeSeries linear(Iterable<Object> iterable) {
        return new TimeSeries(1, iterable);
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
    public double[] at(double[] times) {
        return fun.interpolate(times);
    }

    public double[] at(double time) {
        return fun.interpolate(new double[] { time });
    }

    public double[] at(PyObject datetimes) {
        return at(Evaluator.getActiveEvaluator().convertToTimestamps(datetimes));
    }

    @Override
    public PiecewiseFunction internalFunction() {
        return fun;
    }

    public Object getDatetimes() throws Throwable {
        if (datetimes == null) {
            datetimes = Evaluator.getActiveEvaluator()
                    .convertTimestampsToDatetimes(getTimes());
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

    public TimeSeries slice(double t0, double t1) {
        return new TimeSeries(fun.slice(t0, t1));
    }

    public TimeSeries slice(PyObject t0, PyObject t1) {
        Evaluator evaluator = Evaluator.getActiveEvaluator();
        return slice(evaluator.convertToTimestamp(t0),
                evaluator.convertToTimestamp(t1));
    }

    /** Returns a brief human-readable overview of the time series. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "{ degree = %d, length = %d, mean = %g, stdev = %g }",
                getDegree(), fun.vv.length, getMean(), getStdev());
    }

    public String __str__() {
        return __repr__();
    }

    public String __repr__() {
        return "TimeSeries(" + getDegree() + ", " + Arrays.toString(getTimes())
                + ", " + Arrays.toString(getValues()) + ")";
    }

    public PyObject iter() {
        Evaluator evaluator = Evaluator.getActiveEvaluator();
        return evaluator.izip(getTimes(), getValues());
    }

    public TimeSeries __add__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = a + u[i];
                    }
                }));
    }

    public TimeSeries __radd__(double a) {
        return __add__(a);
    }

    public TimeSeries __add__(TimeSeries other) {
        return new TimeSeries(fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] += u[i];
                    }
                }));
    }

    public TimeSeries __sub__(double a) {
        return __add__(-a);
    }

    public TimeSeries __rsub__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = a - u[i];
                    }
                }));
    }

    public TimeSeries __sub__(TimeSeries other) {
        return new TimeSeries(fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = u[i] - v[i];
                    }
                }));
    }

    public TimeSeries __mul__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = a * u[i];
                    }
                }));
    }

    public TimeSeries __rmul__(double a) {
        return __mul__(a);
    }

    public TimeSeries __mul__(TimeSeries other) {
        return new TimeSeries(fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] *= u[i];
                    }
                }));
    }

    public TimeSeries __floordiv__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = Math.floor(u[i] / a);
                    }
                }));
    }

    public TimeSeries __rfloordiv__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = Math.floor(a / u[i]);
                    }
                }));
    }

    public TimeSeries __floordiv__(TimeSeries other) {
        return new TimeSeries(fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = Math.floor(u[i] / v[i]);
                    }
                }));
    }

    private static final double modulo(double x, double y) {
        double z = Math.IEEEremainder(x, y);
        return (z * y < 0) ? z + y : z;
    }

    public TimeSeries __mod__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = modulo(u[i], a); 
                    }
                }));
    }

    public TimeSeries __rmod__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = modulo(a, u[i]); 
                    }
                }));
    }

    public TimeSeries __mod__(TimeSeries other) {
        return new TimeSeries(fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = modulo(u[i], v[i]); 
                    }
                }));
    }

    public TimeSeries __div__(double a) {
        return __floordiv__(a);
    }

    public TimeSeries __rdiv__(double a) {
        return __rfloordiv__(a);
    }

    public TimeSeries __div__(TimeSeries other) {
        return __floordiv__(other);
    }

    public TimeSeries __truediv__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = u[i] / a;
                    }
                }));
    }

    public TimeSeries __rtruediv__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = a / u[i];
                    }
                }));
    }

    public TimeSeries __truediv__(TimeSeries other) {
        return new TimeSeries(fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = u[i] / v[i];
                    }
                }));
    }

    public TimeSeries __pow__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = Math.pow(u[i],  a);
                    }
                }));
    }

    public TimeSeries __rpow__(double a) {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = Math.pow(a, u[i]);
                    }
                }));
    }

    public TimeSeries __pow__(TimeSeries other) {
        return new TimeSeries(fun.combine(other.fun,
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = Math.pow(u[i],  v[i]);
                    }
                }));
    }

    public TimeSeries __neg__() {
        return new TimeSeries(fun.transform(
                (double[] u, double[] v) -> {
                    for (int i = 0; i < u.length; ++i) {
                        v[i] = -u[i];
                    }
                }));
    }

    public TimeSeries __pos__() {
        return this;
    }

    public TimeSeries __abs__() {
        return new TimeSeries(fun.abs());
    }
}
