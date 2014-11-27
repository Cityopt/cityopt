package eu.cityopt.sim.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.script.ScriptException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test time series access directly and from Python expressions.
 *
 * @author Hannu Rummukainen
 */
public class TestTimeSeries {
    static Evaluator evaluator;
    public static final double delta = 1.0e-12;

    @BeforeClass
    public static void setUp() throws Exception {
        evaluator = new Evaluator();
    }
    
    @Test
    public void timeSeriesExpressions_step() throws Exception {
        testTimeSeriesExpressions(Type.TIMESERIES_STEP);
    }

    @Test
    public void timeSeriesExpressions_linear() throws Exception {
        testTimeSeriesExpressions(Type.TIMESERIES_LINEAR);
    }

    void testTimeSeriesExpressions(Type timeSeriesType) throws Exception {
        Namespace ns = new Namespace(evaluator, Arrays.asList(new String[0]));
        boolean step = (timeSeriesType.getInterpolationDegree() == 0);
        ns.externals.put("a", timeSeriesType);
        ns.externals.put("b", timeSeriesType);

        ZonedDateTime zdt = ZonedDateTime.of(2014, 1, 1,  12, 0, 0,  0, ZoneId.systemDefault());
        double t0 = zdt.toEpochSecond();
        double sec = 1;
        double day = 24 * 60 * 60 * sec;
        ExternalParameters ep = new ExternalParameters(ns);

        double[] ta = new double[] { t0, t0 + day, t0 + day + sec };
        double[] va = new double[] { 1.0, 2.0, 5.0 };
        ep.put("a", evaluator.makeTS(timeSeriesType, ta, va));

        double[] tb = new double[] { t0, t0 + sec, t0 + day };
        double[] vb = new double[] { -4.0, 3.0, -2.0 };
        ep.put("b", evaluator.makeTS(timeSeriesType, tb, vb));

        // Datetime access
        assertEquals(2014, eval("a.datetimes[0].year", ep), delta);
        assertEquals(2, eval("a.datetimes[1].day", ep), delta);
        assertEquals(12, eval("a.datetimes[2].hour", ep), delta);
        assertEquals(1, eval("a.datetimes[2].second", ep), delta);

        // The functions mean, stdev, var, min, max
        if (step) {
            assertEquals(1.00001157394012, eval("a.mean", ep), delta);
            assertEquals(0.00340202971182287, eval("a.stdev", ep), delta);
        } else {
            double f = (ta[2]-ta[1])/(double)(ta[2]-ta[0]);
            assertEquals((1-f) * 1.5 + f * 3.5, eval("a.mean", ep), delta);
            assertEquals(0.2887686695576, eval("a.stdev", ep), delta);
        }
        assertEquals(eval("a.mean", ep), eval("mean(a)", ep), delta);
        assertEquals(eval("a.stdev", ep), eval("stdev(a)", ep), delta);
        assertEquals(eval("a.var", ep), eval("var(a)", ep), delta);
        assertEquals(1, eval("a.min", ep), delta);
        assertEquals(1, eval("min(a)", ep), delta);
        assertEquals(5, eval("a.max", ep), delta);
        assertEquals(5, eval("max(a)", ep), delta);

        // Arithmetic with a scalar constant
        for (int i = 0; i < vb.length; ++i) {
            assertEquals(vb[i], eval("b.values["+i+"]", ep), delta);
            assertEquals(vb[i]+3, eval("(b+3).values["+i+"]", ep), delta);
            assertEquals(3+vb[i], eval("(3+b).values["+i+"]", ep), delta);
            assertEquals(vb[i]-3, eval("(b-3).values["+i+"]", ep), delta);
            assertEquals(3-vb[i], eval("(3-b).values["+i+"]", ep), delta);
            assertEquals(vb[i]*3, eval("(b*3).values["+i+"]", ep), delta);
            assertEquals(3*vb[i], eval("(3*b).values["+i+"]", ep), delta);
            assertEquals(-vb[i], eval("(-b).values["+i+"]", ep), delta);
            assertEquals(+vb[i], eval("(+b).values["+i+"]", ep), delta);
        }

        // abs function
        assertEquals(Math.abs(vb[0]), eval("abs(b).values[0]", ep), delta);
        assertEquals(tb[0], eval("abs(b).times[0]", ep), delta);
        if (step) {
            assertEquals(Math.abs(vb[1]), eval("abs(b).values[1]", ep), delta);
            assertEquals(Math.abs(vb[2]), eval("abs(b).values[2]", ep), delta);

            assertEquals(tb[1], eval("abs(b).times[1]", ep), delta);
            assertEquals(tb[2], eval("abs(b).times[2]", ep), delta);

            assertEquals(3, eval("len(abs(b).values)", ep), delta);
        } else {
            assertEquals(0.0, eval("abs(b).values[1]", ep), delta);
            assertEquals(Math.abs(vb[1]), eval("abs(b).values[2]", ep), delta);
            assertEquals(0.0, eval("abs(b).values[3]", ep), delta);
            assertEquals(Math.abs(vb[2]), eval("abs(b).values[4]", ep), delta);

            assertEquals(t0 + (4 / 7.0), eval("abs(b).times[1]", ep), delta);
            assertEquals(tb[1], eval("abs(b).times[2]", ep), delta);
            assertEquals(t0 + 51840.4, eval("abs(b).times[3]", ep), delta);
            assertEquals(tb[2], eval("abs(b).times[4]", ep), delta);

            assertEquals(5, eval("len(abs(b).values)", ep), delta);
        }

        // Arithmetic between two time series
        assertEquals(va[0] + vb[0], eval("(a+b).values[0]", ep), delta);
        double f = step ? 0 : (tb[1]-ta[0])/(double)(ta[1]-ta[0]);
        assertEquals((1-f)*va[0] + f*va[1] - vb[1],
                     eval("(a-b).values[1]", ep), delta);
        assertEquals(((1-f)*va[0] + f*va[1]) * vb[1],
                eval("(a*b).values[1]", ep), delta);
        if (step) {
            assertEquals(va[1], eval("(a+b).values[2]", ep), delta);
        } else {
            assertEquals(va[1] + vb[2], eval("(a+b).values[2]", ep), delta);
            assertEquals(va[1], eval("(a+b).values[3]", ep), delta);
        }
        int i = step ? 3 : 4;
        assertEquals(va[2], eval("(a+b).values["+i+"]", ep), delta);
        assertEquals(-va[2], eval("(b-a).values["+i+"]", ep), delta);
        assertEquals(0.0, eval("(a*b).values["+i+"]", ep), delta);
        assertEquals(i+1, eval("len((a*b).values)", ep), delta);

        assertEquals(ta[0], eval("(a+b).times[0]", ep), delta);
        assertEquals(tb[1], eval("(a-b).times[1]", ep), delta);
        assertEquals(ta[1], eval("(a*b).times[2]", ep), delta);
        if (step) {
            assertEquals(ta[2], eval("(a*b).times[3]", ep), delta);
        } else {
            assertEquals(ta[1], eval("(a*b).times[3]", ep), delta);
            assertEquals(ta[2], eval("(a+b).times[4]", ep), delta);
        }

        tb = new double[] { t0 + day + sec/2, t0 + day + sec*3/4 };
        vb = new double[] { -5.0, -6.0 };
        ep.put("b", evaluator.makeTS(timeSeriesType, tb, vb));
        f = step ? 0 : (tb[0]-ta[1])/(double)(ta[2]-ta[1]);
        i = step ? 2 : 3;
        double x = ((1-f)*va[1] + f*va[2]) * vb[0];
        assertEquals(x, eval("(a*b).values["+i+"]", ep), delta);
        if (step) {
            assertEquals(va[1] * vb[0], eval("sum((a*b).values)", ep), delta);
        } else {
            f = (tb[1] - ta[1])/(double)(ta[2]-ta[1]);
            assertEquals(x + ((1-f)*va[1] + f*va[2])*vb[1],
                    eval("sum((a*b).values)", ep), delta);
        }

        tb = new double[] { t0 + 2*day };
        vb = new double[] { 11.0 };
        ep.put("b", evaluator.makeTS(timeSeriesType, tb, vb));
        i = (int)eval("len((a*b).values)", ep) - 1;
        assertEquals(step ? 3 : 5, i);
        assertEquals(0.0, eval("sum((a*b).values)", ep), delta);
        assertEquals(vb[0], eval("(a+b).values["+i+"]", ep), delta);
        assertEquals(vb[0], eval("(b-a).values["+i+"]", ep), delta);
        assertEquals(va[1], eval("(a+b).values[1]", ep), delta);
        assertEquals(-va[1], eval("(b-a).values[1]", ep), delta);

        // Interpolation and integration expressions
        for (int j = 0; j < ta.length; ++j) {
            assertEquals(va[j], eval("a.at("+ta[j]+")[0]", ep), delta);
        }
        assertEquals(3, eval("len(a.at([1,2,3]))", ep), delta);
        assertEquals(0, eval("sum(a.at([1,2,3]))", ep), delta);
        assertEquals(va[0], eval("a.at(datetime(2014,1,1,12,0,0))[0]", ep), delta);
        assertEquals(va[1],
                eval("a.at([datetime(2014,1,d,12,0,0) for d in [1,2]])[1]", ep), delta);
        f = step ? 0.0 : 0.5;
        assertEquals((1-f)*va[0] + f*va[1],
                eval("a.at(datetime(2014,1,2,0,0,0))[0]", ep), delta);
        assertEquals((1-f)*va[1] + f*va[2],
                eval("integrate(a, "+ta[1]+", "+ta[2]+")", ep), delta);
        assertEquals(0.5 * ((1-f)*va[1] + f*va[2]),
                eval("integrate(a, "+ta[1]+", "+(ta[2]+1)+", 2)", ep), delta);
        assertEquals((1-f)*va[0] + f*va[1],
                eval("integrate(a, datetime(2014,1,1,12), "
                        + "datetime(2014,1,2,12), 86400)", ep), delta);

        // Time series constructors
        assertEquals(2, eval("len(TimeSeries(0, [0.0, 1.0], [2.0, 4.0]).values)", ep), delta);
        assertEquals(29, eval("sum(TimeSeries(0, [datetime.fromtimestamp(9), "
                            + "datetime.fromtimestamp(20)], [2.0, 4.0]).times)", ep), delta);
        String constructor = step ? "step" : "linear";
        assertEquals(2, eval("len(TimeSeries." + constructor
                            + "((t, 2*t) for t in [1, 2]).values)", ep), delta);
        assertEquals(110, eval("sum(TimeSeries."+constructor+"((t, 2*t)"
                            + " for t in range(1, 11)).values)", ep), delta);
        int degree = step ? 0 : 1;
        assertEquals(degree, eval("TimeSeries."+constructor+"((t, 2*t)"
                + " for t in range(1, 11)).degree", ep), delta);
        assertEquals(va[0]+va[1]+va[2],
                eval("sum(TimeSeries."+constructor+"(a.iter()).values)", ep), delta);
        assertEquals(ta[0]+ta[1]+ta[2], eval("sum(t for t, v in a.iter())", ep), delta);

        // Slicing
        assertEquals(0, eval("len(a.slice(0, 1).values)", ep), delta);
        assertEquals(1, eval("len(a.slice("+ta[0]+", "+ta[0]+").values)", ep), delta);
        assertEquals(va[0], eval("a.slice("+ta[0]+", "+ta[0]+").values[0]", ep), delta);
        assertEquals(2, eval("len(a.slice(datetime(2014,1,1,12), "+ta[1]+").values)", ep), delta);
        assertEquals(va[0]+va[1], eval(
                "sum(a.slice(datetime(2014,1,1,12), datetime(2014,1,2,12)).values)", ep), delta);
        assertEquals(3, eval("len(a.slice("+(ta[0]+sec)+", "
                                    +(ta[1]+0.5*sec)+").values)", ep), delta);
        f = step ? 0.0 : 0.375;
        assertEquals((1-f)*va[0] + f*va[1],
                eval("mean(a.slice("+(0.75*ta[0]+0.25*ta[1])+", "
                                +(0.5*ta[0]+0.5*ta[1])+"))", ep), delta);
    }

    private double eval(String expression, EvaluationContext context)
            throws ScriptException, InvalidValueException {
        return new DoubleExpression(expression, evaluator).evaluate(context);
    }

    /** Generate subsequences of a time series for testing. */
    Collection<TimeSeriesI> generateSubTimeSeries(
            double[] times, double[] values, Type timeSeriesType, int asDegree) {
        List<TimeSeriesI> list = new ArrayList<TimeSeriesI>(); 
        for (int i0 = 0; i0 < times.length; ++i0) {
            for (int i1 = i0; i1 < times.length; ++i1) {
                double[] t = new double[i1 - i0];
                double[] v = new double[i1 - i0];
                System.arraycopy(times, i0, t, 0, i1 - i0);
                System.arraycopy(values, i0, v, 0, i1 - i0);
                TimeSeriesI ts = evaluator.makeTS(timeSeriesType, times, values);
                if (timeSeriesType.getInterpolationDegree() != asDegree) {
                    // Convert a step function to a piecewise linear
                    // representation with vertical segments.
                    if (ts.getValues().length > 0) {
                        TimeSeriesI b = evaluator.makeTS(
                                Type.TIMESERIES_LINEAR,
                                new double[] { times[0] },
                                new double[] { 0.0 });
                        ts = ((TimeSeries) ts).__add__((TimeSeries) b);
                    }
                }
                list.add(ts);
            }
        }
        return list;
    }

    static double[] sampleTimes = new double[] { 0, 100, 110, 111, 10000, 11000 };
    static double[] sampleValues = new double[] { -10, 2, 5, -9, 0, 3 };
    static double[][] interpolationSequences = new double[][] {
        // interpolation points including time series points
        { -10, -1, 0, 10, 11, 200, 10000, 11000, 100000, 200000 },
        // interpolation points without time series points
        { -10, -1, 200, 10100, 100000, 200000 }
    };
    static double[] offsetSampleTimes = offsetTimes(sampleTimes, 10);

    private static double[] offsetTimes(double[] tt, double offset) {
        double[] tto = new double[tt.length];
        for (int i = 0; i < tt.length; ++i) {
            tto[i] = tt[i] + offset;
        }
        return tto;
    }

    @Test
    public void interpolate_step() throws Exception {
        testInterpolation(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 0));
    }

    @Test
    public void interpolate_linear() throws Exception {
        testInterpolation(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));
    }

    @Test
    public void interpolate_linearFromStep() throws Exception {
        testInterpolation(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 1));
    }

    /**
     * Check if TimeSeries.at returns correct results.
     * We get baseline results from the inner class SimpleInterpolator.
     */
    void testInterpolation(Collection<TimeSeriesI> tss) throws Exception {
        for (TimeSeriesI ts : tss)  {
            SimpleInterpolator si = new SimpleInterpolator(ts);
            for (double[] sequence : interpolationSequences) {
                for (int i0 = 0; i0 < sequence.length; ++i0) {
                    for (int i1 = i0; i1 < sequence.length; ++i1) {
                        double[] at = new double[i1 - i0];
                        System.arraycopy(sequence, i0, at, 0, i1 - i0);

                        double[] vi = ts.at(at);

                        for (int i = 0; i < at.length; ++i) {
                            assertEquals(si.interpolate(at[i]), vi[i], delta);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void interpolateAtVerticalEdge() throws Exception {
        double[] times = new double[] { 0, 1, 1, 2, 4, 4 };
        double[] values = new double[] { 1, 0, 3, 2, 8, 11 };
        TimeSeriesI ts = evaluator.makeTS(Type.TIMESERIES_LINEAR, times, values);
        assertArrayEquals(new double[] { 0, 3, 5, 5, 8, 11 },
                ts.at(new double[] { 1, 1, 3, 3, 4, 4 }), delta);
        assertArrayEquals(new double[] { 3, 5, 11 },
                ts.at(new double[] { 1, 3, 4, }), delta);
    }

    @Test
    public void integrate_step() throws Exception {
        testIntegration(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 0));
    }

    @Test
    public void integrate_linear() throws Exception {
        testIntegration(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));
    }

    @Test
    public void integrate_linearFromStep() throws Exception {
        testIntegration(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 1));
    }

    /**
     * Check if integrate returns correct results.
     * We get baseline results from the inner class SimpleInterpolator.
     */
    void testIntegration(Collection<TimeSeriesI> tss) throws Exception {
        double[] scales = new double[] { 0, 1, 1000 };
        for (TimeSeriesI ts : tss) {
            PiecewiseFunction fun = ts.internalFunction();
            SimpleInterpolator si = new SimpleInterpolator(ts);
            for (double[] sequence : interpolationSequences) {
                for (double t0 : sequence) {
                    for (double t1 : sequence) {
                        for (double scale : scales) {
                            double s = fun.integrate(t0, t1, scale);
                            double c = si.integrate(t0, t1, scale);
                            assertEquals(c, s, delta);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void sum_step_step() throws Exception {
        testSums(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 0),
                generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 0));

        testSums(generateSubTimeSeries(
                offsetSampleTimes, sampleValues, Type.TIMESERIES_STEP, 0),
                generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 0));
    }

    @Test
    public void sum_linear_linear() throws Exception {
        testSums(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1),
                generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));

        testSums(generateSubTimeSeries(
                offsetSampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1),
                generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));

        testSums(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 1),
                generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));

        testSums(generateSubTimeSeries(
                offsetSampleTimes, sampleValues, Type.TIMESERIES_STEP, 1),
                generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));
    }

    @Test
    public void sum_step_linear() throws Exception {
        testSums(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 0),
                generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));

        testSums(generateSubTimeSeries(
                offsetSampleTimes, sampleValues, Type.TIMESERIES_STEP, 0),
                generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));

        testSums(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 0),
                generateSubTimeSeries(
                offsetSampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));
    }

    /**
     * Check if summing two time series works correctly.
     * We check the results by interpolating at a number of points, and
     * comparing with the sum of SimpleInterpolator results from the
     * two summands.
     */
    void testSums(Collection<TimeSeriesI> tss1, Collection<TimeSeriesI> tss2) {
        double[] checkTimes = listNearbyTimes(sampleTimes, 1);

        for (TimeSeriesI ts1 : tss1)  {
            SimpleInterpolator si1 = new SimpleInterpolator(ts1);
            double[] si1values = si1.interpolate(checkTimes);
            double end1 = si1.endTime();

            for (TimeSeriesI ts2 : tss2) {
                SimpleInterpolator si2 = new SimpleInterpolator(ts2);
                double[] si2values = si2.interpolate(checkTimes);
                double end2 = si2.endTime();

                TimeSeriesI sum = ((TimeSeries) ts1).__add__(
                        (TimeSeries) ts2);
                double[] sumValues = sum.at(checkTimes);

                for (int i = 0; i < sumValues.length; ++i) {
                    double v1 = si1values[i];
                    if (checkTimes[i] == end1 && end1 < end2) {
                        v1 = 0;
                    }
                    double v2 = si2values[i];
                    if (checkTimes[i] == end2 && end2 < end1) {
                        v2 = 0;
                    }
                    if (Math.abs(v1 + v2 - sumValues[i]) > delta) {
                        System.err.println();
                        System.err.println(ts1.internalFunction());
                        System.err.println(ts2.internalFunction());
                        System.err.println(sum.internalFunction());
                        System.err.println("Sum at t="+checkTimes[i]
                                +" = "+sumValues[i]+" != "
                                +v1+" + "+v2+" = "+(v1+v2));
                    }
                    assertEquals(v1 + v2, sumValues[i], delta);
                }
            }
        }
    }

    /**
     * Given an array 'times', returns an ordered array containing
     * t, t-offset and t+offset for each t in times.
     */
    static double[] listNearbyTimes(double[] times, double offset) {
        SortedSet<Double> set = new TreeSet<Double>();

        for (double t : times) {
            set.add(t - offset);
            set.add(t);
            set.add(t + offset);
        }

        double[] tt = new double[set.size()];
        int i = 0;
        for (Double t : set) {
            tt[i++] = t;
        }
        return tt;
    }

    @Test
    public void sliceAtVerticalEdge() throws Exception {
        double[] times = new double[] { 0, 1, 1, 2, 4, 4 };
        double[] values = new double[] { 1, 0, 3, 2, 8, 11 };
        TimeSeries ts = (TimeSeries) evaluator.makeTS(Type.TIMESERIES_LINEAR, times, values);
        assertArrayEquals(new double[] { 0, 1, 1 }, ts.slice(0, 1).getTimes(), delta);
        assertArrayEquals(new double[] { 1, 0, 3 }, ts.slice(0, 1).getValues(), delta);
        assertArrayEquals(new double[] { 1, 2, 3 }, ts.slice(1, 3).getTimes(), delta);
        assertArrayEquals(new double[] { 3, 2, 5 }, ts.slice(1, 3).getValues(), delta);
        assertArrayEquals(times, ts.slice(0, 4).getTimes(), delta);
        assertArrayEquals(values, ts.slice(0, 4).getValues(), delta);
    }

    @Test
    public void slice_step() throws Exception {
        testSlicing(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 0));
    }

    @Test
    public void slice_linear() throws Exception {
        testSlicing(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_LINEAR, 1));
    }

    @Test
    public void slice_linearFromStep() throws Exception {
        testSlicing(generateSubTimeSeries(
                sampleTimes, sampleValues, Type.TIMESERIES_STEP, 1));
    }

    /**
     * Check if TimeSeries.slice returns correct results.
     * We get baseline results by interpolating with SimpleInterpolator.
     */
    void testSlicing(Collection<TimeSeriesI> tss) throws Exception {
        for (TimeSeriesI ts : tss)  {
            SimpleInterpolator si = new SimpleInterpolator(ts);
            double[] sliceTimes = listNearbyTimes(ts.getTimes(), 1);
            double[] checkTimes = listNearbyTimes(sliceTimes, 1);
            for (int i0 = 0; i0 < sliceTimes.length; ++i0) {
                double t0 = sliceTimes[i0];
                for (int i1 = i0; i1 < sliceTimes.length; ++i1) {
                    double t1 = sliceTimes[i1];
                    TimeSeriesI slice = ((TimeSeries) ts).slice(t0, t1);
                    double[] vi = slice.at(checkTimes);

                    for (int i = 0; i < checkTimes.length; ++i) {
                        double t = checkTimes[i];
                        double v = (t0 <= t && t <= t1) ? si.interpolate(t) : 0.0;
                        assertEquals(v, vi[i], delta);
                    }
                }
            }
        }
    }

    /**
     * A simple, slow and obviously correct interpolator for comparison with the
     * more complex ones in the PiecewiseFunction classes.
     */
    static class SimpleInterpolator {
        final double[] times;
        final double[] values;
        final int degree;

        SimpleInterpolator(TimeSeriesI ts) {
            this.times = ts.getTimes().clone();
            this.values = ts.getValues().clone();
            this.degree = ts.getDegree();
        }

        public double endTime() {
            return times.length > 0 ? times[times.length - 1] : Double.NEGATIVE_INFINITY;
        }

        double[] interpolate(double[] tt) {
            double[] vv = new double[tt.length];
            for (int i = 0; i < tt.length; ++i) {
                vv[i] = interpolate(tt[i]);
            }
            return vv;
        }

        double interpolate(double t) {
            int n = times.length;
            if (n == 0) {
                return 0.0;
            }
            if (t < times[0]) {
                return 0.0;
            }
            for (int i = 0; i < n-1; ++i) {
                if (t >= times[i] && t < times[i+1]) {
                    if (degree == 0) {
                        return values[i];
                    } else {
                        double f = (t - times[i]) / (double) (times[i+1] - times[i]);
                        return (1-f) * values[i] + f * values[i+1];
                    }
                }
            }
            if (t == times[n-1]) {
                return values[n-1];
            } else if (t > times[n-1]) {
                return 0.0;
            } else {
                throw new IllegalStateException();
            }
        }

        double integrate(double t0, double t1, double scale) {
            int n = times.length;
            if (n == 0) {
                return 0.0;
            }
            if (t1 < t0) {
                return -integrate(t1, t0, scale);
            }
            if (scale == 0) {
                if (t1 < times[0]) {
                    return 0.0;
                }
                if (t0 > times[n-1]) {
                    return 0.0;
                }
                if (t0 < times[0]) {
                    t0 = times[0];
                }
                if (t1 > times[n-1]) {
                    t1 = times[n-1];
                }
                if (t0 == t1) {
                    return interpolate(t0);
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            } else {
                double s = 0.0;
                boolean on = (n > 1 && t0 < times[0] && t1 >= times[0]);
                for (int i = 0; i < n-1; ++i) {
                    if (t0 >= times[i] && t0 < times[i+1]) {
                        assert !on;
                        if (t1 >= times[i] && t1 < times[i+1]) {
                            s = (t1 - t0) * 0.5 * (interpolate(t0) + interpolate(t1));
                            break;
                        }
                        if (degree == 0) {
                            s = (times[i+1] - t0) * values[i];
                        } else {
                            s = (times[i+1] - t0) * 0.5 * (interpolate(t0) + values[i+1]);
                        }
                        on = true;
                    } else if (t1 >= times[i] && t1 < times[i+1]) {
                        assert on;
                        if (degree == 0) {
                            s += (t1 - times[i]) * values[i];
                        } else {
                            s += (t1 - times[i]) * 0.5 * (values[i] + interpolate(t1));
                        }
                        on = false;
                        break;
                    } else if (t0 < times[i] && t1 >= times[i+1]) {
                        assert on;
                        if (degree == 0) {
                            s += (times[i+1] - times[i]) * values[i];
                        } else {
                            s += (times[i+1] - times[i]) * 0.5 * (values[i] + values[i+1]);
                        }
                    } else {
                        assert !on;
                    }
                }
                assert !on || t1 >= times[n-1];
                return s / scale;
            }
        }
    }
}
