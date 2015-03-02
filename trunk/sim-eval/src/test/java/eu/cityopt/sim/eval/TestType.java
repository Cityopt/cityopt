package eu.cityopt.sim.eval;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestType {
    static EvaluationSetup es;

    static final String TS123 =
            "[\"2015-01-01T00:00:01Z\", \"2015-01-01T00:00:02Z\", \"2015-01-01T00:00:03Z\"]";

    @BeforeClass
    public static void setup() throws Exception {
        es = new EvaluationSetup(new Evaluator(), Instant.ofEpochMilli(1420070400000L));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void parseListOfInteger() throws Exception {
        assertEquals(Arrays.asList(1, 2, 3), Type.LIST_OF_INTEGER.parse("[1, 2, 3]", es));
        assertEquals("[1, 2, 3]", Type.LIST_OF_INTEGER.format(Arrays.asList(1, 2, 3), es));
        assertEquals(Arrays.asList(), Type.LIST_OF_INTEGER.parse("[]", es));
        assertEquals("[]", Type.LIST_OF_INTEGER.format(Arrays.asList(), es));
        assertEquals(Arrays.asList(1, 2, 3), Type.LIST_OF_INTEGER.parse("[1.0, 2.0, 3.0]", es));
        assertTrue(Type.LIST_OF_INTEGER.isCompatible(Type.LIST_OF_INTEGER.parse("[1, 2, 3]", es)));
        assertTrue(Type.LIST_OF_INTEGER.isCompatible(Type.LIST_OF_INTEGER.parse("[1.0, 2.0, 3.0]", es)));
        assertTrue(Type.LIST_OF_INTEGER.isCompatible(es.evaluator.eval("[1, 2, 3]", es)));
        assertTrue(((List<Integer>)Type.LIST_OF_INTEGER.parse("[1.0, 2.0, 3.0]", es)).get(0)
                instanceof Integer);
    }

    @Test(expected=ParseException.class)
    public void parseListOfInteger_MixedList() throws Exception {
        System.out.println(Type.LIST_OF_INTEGER.parse("[1, \"2\", \"x\"]", es));
    }

    @Test(expected=ParseException.class)
    public void parseListOfInteger_Scalar() throws Exception {
        System.out.println(Type.LIST_OF_INTEGER.parse("1", es));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void parseListOfDouble() throws Exception {
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), Type.LIST_OF_DOUBLE.parse("[1, 2, 3]", es));
        assertEquals("[1.0, 2.0, 3.0]", Type.LIST_OF_DOUBLE.format(Arrays.asList(1.0, 2.0, 3.0), es));
        assertEquals(Arrays.asList(), Type.LIST_OF_DOUBLE.parse("[]", es));
        assertEquals("[]", Type.LIST_OF_DOUBLE.format(Arrays.asList(), es));
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), Type.LIST_OF_DOUBLE.parse("[1.0, 2.0, 3.0]", es));
        assertTrue(Type.LIST_OF_DOUBLE.isCompatible(Type.LIST_OF_DOUBLE.parse("[1, 2, 3]", es)));
        assertTrue(Type.LIST_OF_DOUBLE.isCompatible(Type.LIST_OF_DOUBLE.parse("[1.0, 2.0, 3.0]", es)));
        assertTrue(Type.LIST_OF_DOUBLE.isCompatible(es.evaluator.eval("[1, 2, 3]", es)));
        assertTrue(Type.LIST_OF_DOUBLE.isCompatible(es.evaluator.eval("[1.0, 2.0, 3.0]", es)));
        assertTrue(((List<Double>)Type.LIST_OF_DOUBLE.parse("[1, 2, 3]", es)).get(0)
                instanceof Double);
    }

    @Test(expected=ParseException.class)
    public void parseListOfDouble_MixedList() throws Exception {
        System.out.println(Type.LIST_OF_DOUBLE.parse("[1, \"2\", \"x\"]", es));
    }

    @Test(expected=ParseException.class)
    public void parseListOfDouble_Scalar() throws Exception {
        System.out.println(Type.LIST_OF_DOUBLE.parse("1.0", es));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void parseListOfTimestamp() throws Exception {
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), Type.LIST_OF_TIMESTAMP.parse("[1, 2, 3]", es));
        assertEquals(TS123, Type.LIST_OF_TIMESTAMP.format(Arrays.asList(1.0, 2.0, 3.0), es));
        assertEquals(Arrays.asList(), Type.LIST_OF_TIMESTAMP.parse("[]", es));
        assertEquals("[]", Type.LIST_OF_TIMESTAMP.format(Arrays.asList(), es));
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), Type.LIST_OF_TIMESTAMP.parse(TS123, es));
        assertTrue(Type.LIST_OF_TIMESTAMP.isCompatible(Type.LIST_OF_TIMESTAMP.parse("[1, 2, 3]", es)));
        assertTrue(Type.LIST_OF_TIMESTAMP.isCompatible(Type.LIST_OF_TIMESTAMP.parse("[1.0, 2.0, 3.0]", es)));
        assertTrue(Type.LIST_OF_TIMESTAMP.isCompatible(es.evaluator.eval("[1, 2, 3]", es)));
        assertTrue(Type.LIST_OF_TIMESTAMP.isCompatible(es.evaluator.eval("[1.0, 2.0, 3.0]", es)));
        assertFalse(Type.LIST_OF_TIMESTAMP.isCompatible(es.evaluator.eval(TS123, es)));
        assertTrue(((List<Double>)Type.LIST_OF_TIMESTAMP.parse("[1, 2, 3]", es)).get(0)
                instanceof Double);
    }

    @Test(expected=ParseException.class)
    public void parseListOfTimestamp_MixedList() throws Exception {
        System.out.println(Type.LIST_OF_TIMESTAMP.parse("[1, \"2\", \"x\"]", es));
    }

    @Test(expected=ParseException.class)
    public void parseListOfTimestamp_Scalar() throws Exception {
        System.out.println(Type.LIST_OF_TIMESTAMP.parse("1.0", es));
    }

    @Test
    public void intExpressions() throws Exception {
        testExpression(Type.INTEGER, Integer.MIN_VALUE, "-2147483648");
        testExpression(Type.INTEGER, -12345, "-12345"); 
        testExpression(Type.INTEGER, -1, "-1"); 
        testExpression(Type.INTEGER, 0, "0");
        testExpression(Type.INTEGER, 1, "1"); 
        testExpression(Type.INTEGER, 12345, "12345"); 
        testExpression(Type.INTEGER, Integer.MAX_VALUE, "2147483647"); 
    }

    @Test
    public void doubleExpressions() throws Exception {
        testExpression(Type.DOUBLE, Double.NaN, "float('nan')");
        testExpression(Type.DOUBLE, Double.NEGATIVE_INFINITY, "float('-inf')"); 
        testExpression(Type.DOUBLE, Double.POSITIVE_INFINITY, "float('inf')"); 
        testExpression(Type.DOUBLE, -1.0e9, "-1.0E9", "-1000000000"); 
        testExpression(Type.DOUBLE, -1.0, "-1.0", "-1"); 
        testExpression(Type.DOUBLE, 0.0, "0.0", "0");
        testExpression(Type.DOUBLE, 1.0, "1.0", "1"); 
        testExpression(Type.DOUBLE, 1.0e9, "1.0E9", "1000000000");
    }

    @Test
    public void stringExpressions() throws Exception {
        testExpression(Type.STRING, "", "''", "\"\"");
        testExpression(Type.STRING, "a", "'a'", "\"a\"");
        testExpression(Type.STRING, "'", "\"'\"");
        testExpression(Type.STRING, "4\n3\r2", "'4\\n3\\r2'");
    }

    @Test
    public void timeSeriesExpressions() throws Exception {
        testTSExpression(Type.TIMESERIES_STEP, es.evaluator.makeTS(
                Type.TIMESERIES_STEP, new double[] { 1.0 }, new double[] { 2.0 }),
                "TimeSeries(0, [1.0], [2.0])");
        testTSExpression(Type.TIMESERIES_STEP, es.evaluator.makeTS(
                Type.TIMESERIES_STEP, new double[] { 1.0, 2.0 }, new double[] { 2.0, 4.0 }),
                "TimeSeries(0, [1.0, 2.0], [2.0, 4.0])");
        testTSExpression(Type.TIMESERIES_LINEAR, es.evaluator.makeTS(
                Type.TIMESERIES_LINEAR, new double[] { 1.0, 2.0 }, new double[] { 2.0, 4.0 }),
                "TimeSeries(1, [1.0, 2.0], [2.0, 4.0])");
    }

    @Test
    public void timestampExpressions() throws Exception {
        // Jython datetime module uses the Java default timezone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        testExpression(Type.TIMESTAMP, 1.0, "1.0", "\"2015-01-01T00:00:01Z\"", "datetime(2015,1,1,0,0,1,0)");
        testExpression(Type.TIMESTAMP, 2.0, "2.0", "\"2015-01-01T00:00:02Z\"", "datetime(2015,1,1,0,0,2,0)");

        testExpression(Type.LIST_OF_TIMESTAMP, Arrays.asList(1.0, 2.0, 3.0), "[1.0, 2.0, 3.0]", TS123);
    }

    public void testExpression(Type type, Object value, String... expressions) throws Exception {
        assertEquals(expressions[0], type.toExpression(value, es));
        for (String expression : expressions) {
            assertEquals(value, type.evalExpression(expression, es));
        }
    }

    public void testTSExpression(Type type, TimeSeriesI value, String expression) throws Exception {
        assertEquals(expression, type.toExpression(value, es));
        TimeSeriesI value2 = (TimeSeriesI) type.evalExpression(expression, es);
        final double delta = 1.0e-12;
        assertEquals(value.getDegree(), value2.getDegree());
        assertArrayEquals(value.getTimes(), value2.getTimes(), delta);
        assertArrayEquals(value.getValues(), value2.getValues(), delta);
    }
}
