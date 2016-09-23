package eu.cityopt.sim.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;

/**
 * Time series representation for expression evaluation in Python.
 * In Java code, please use the TimeSeriesI interface.
 * <p>
 * See CITYOPT Planning Tool Expression Language documentation for the
 * supported Python operations.
 * @author Hannu Rummukainen
 */
public class TimeSeries implements TimeSeriesI {
    private final PiecewiseFunction fun;
    private volatile Integer externalId;

    private PyObject datetimes;

    private boolean statisticsOk;
    private double mean;
    private double var;
    private double max;
    private double min;

    public TimeSeries(PiecewiseFunction function) {
        this.fun = function;
    }

    /**
     * Constructs a time series representation for use in expressions.
     *
     * @param degree
     *            0 for piecewise constant, 1 for piecewise linear interpolation.
     * @param times
     *            the defined time points, seconds from simulation time origin.
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
        double[] times = Evaluator.getActiveEvaluator().convertToSimtimes(datetimes);
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
                pair.time = evaluator.convertToSimtime(first);
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
        return fun.getTimes();
    }

    @Override
    public double[] getValues() {
        return fun.getValues();
    }

    @Override
    public int getDegree() {
        return fun.getDegree();
    }

    @Override
    public Integer getTimeSeriesId() {
        return externalId;
    }

    @Override
    public void setTimeSeriesId(Integer value) {
        externalId = value;
    }

    @Override
    public double[] at(double[] times) {
        return fun.interpolate(times);
    }

    public double[] at(double time) {
        return fun.interpolate(new double[] { time });
    }

    public double[] at(PyObject datetimes) {
        return at(Evaluator.getActiveEvaluator().convertToSimtimes(datetimes));
    }

    @Override
    public PiecewiseFunction internalFunction() {
        return fun;
    }

    public Object getDatetimes() throws Throwable {
        if (datetimes == null) {
            datetimes = Evaluator.getActiveEvaluator()
                    .convertSimtimesToDatetimes(getTimes());
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
        return slice(evaluator.convertToSimtime(t0),
                evaluator.convertToSimtime(t1));
    }

    /** Returns a brief human-readable overview of the time series. */
    @Override
    public String toString() {
        return "TimeSeries(" + getDegree() + ", " + Arrays.toString(getTimes())
                    + ", " + Arrays.toString(getValues()) + ")";
    }

    public String __str__() {
        return __repr__();
    }

    @Override
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

    @Override
    public int hashCode() {
        return fun.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TimeSeries)) {
            return false;
        } else if (other == this) {
            return true;
        } else {
            TimeSeries otherTS = (TimeSeries) other;
            return fun.equals(otherTS.internalFunction());
        }
    }
}
