package eu.cityopt.sim.eval;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestType {
    static EvaluationSetup es;

    static final String TS123 =
            "[\"2015-01-01T00:00:01Z\", \"2015-01-01T00:00:02Z\", \"2015-01-01T00:00:03Z\"]";

    @BeforeClass
    public static void setup() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
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
        assertEquals(expressions[0], type.toConstantExpression(value, es));
        for (String expression : expressions) {
            assertEquals(value, type.evalConstantExpression(expression, es));
        }
    }

    public void testTSExpression(Type type, TimeSeriesI value, String expression) throws Exception {
        assertEquals(expression, type.toConstantExpression(value, es));
        TimeSeriesI value2 = (TimeSeriesI) type.evalConstantExpression(expression, es);
        final double delta = 1.0e-12;
        assertEquals(value.getDegree(), value2.getDegree());
        assertArrayEquals(value.getTimes(), value2.getTimes(), delta);
        assertArrayEquals(value.getValues(), value2.getValues(), delta);
    }

    @Test
    public void dynamicType() throws Exception {
        testDynamic(new Integer(1), "1");
        assertEquals("11", Type.DYNAMIC.format(new Short((short) 11), es));
        assertEquals("11", Type.DYNAMIC.format(new Byte((byte) 11), es));

        testDynamic(new Double(1), "1.0");
        assertEquals("11.0", Type.DYNAMIC.format(new Float(11.0), es));

        BigInteger b = new BigInteger(new byte[] { 1, 0,0,0,0, 0,0,0,3 });
        testDynamic(b, "18446744073709551619L");
        assertEquals(b, Type.DYNAMIC.parse("18446744073709551619", es));

        testDynamic("foo", "'foo'");
        assertEquals("foo", Type.DYNAMIC.parse("\"foo\"", es));
        testDynamic("foo\n", "'foo\\n'");

        testDynamic(Date.from(Instant.parse("2015-01-01T00:00:00Z")), "datetime(2015,1,1,0,0,0)");
        assertEquals(Date.from(Instant.parse("2015-01-01T00:00:00Z")),
                Type.DYNAMIC.parse("todatetime(0)", es));

        testDynamic(es.evaluator.makeTS(Type.TIMESERIES_STEP,
                new double[] { 1.0 }, new double[] { 2.0 }),
                "TimeSeries(0, [1.0], [2.0])");
        testDynamic(es.evaluator.makeTS(Type.TIMESERIES_STEP,
                new double[] { 1.0, 2.0 }, new double[] { 2.0, 4.0 }),
                "TimeSeries(0, [1.0, 2.0], [2.0, 4.0])");
        testDynamic(es.evaluator.makeTS(Type.TIMESERIES_LINEAR,
                new double[] { 1.0, 2.0 }, new double[] { 2.0, 4.0 }),
                "TimeSeries(1, [1.0, 2.0], [2.0, 4.0])");

        testDynamic(null, "None");

        testDynamic(Arrays.asList(1, 2, 3), "[1, 2, 3]");
        testDynamic(Arrays.asList(1.0, 2.0, 3.0), "[1.0, 2.0, 3.0]");
        List<Object> list = new ArrayList<Object>();
        list.add("foo");
        list.add(Arrays.asList(3, 9));
        testDynamic(list, "['foo', [3, 9]]");

        Set<Object> set = new TreeSet<Object>();
        set.add(2.0);
        set.add(1.0);
        testDynamic(set, "set([1.0, 2.0])");

        Map<Object,Object> map = new HashMap<>();
        map.put("a", 3.0);
        map.put(9, list);
        testDynamic(map, "{'a': 3.0, 9: ['foo', [3, 9]]}");
        assertEquals(map, Type.DYNAMIC.parse("dict([('a', 3.0), (9, ['foo', [3, 9]])])", es));

        // Python tuple is represented as Java List
        assertEquals(list, Type.DYNAMIC.parse("('foo', [3, 9])", es));
        assertEquals(Arrays.asList(3.0, 5.0), Type.DYNAMIC.parse("(3.0, 5.0)", es));

        assertFalse(Type.DYNAMIC.isCompatible(this));
    }

    public void testDynamic(Object value, String expression) throws Exception {
        Object parsed = Type.DYNAMIC.parse(expression, es);
        if (parsed instanceof java.sql.Timestamp) {
            assertEquals(value, Date.from(((java.sql.Timestamp) parsed).toInstant()));
        } else if (parsed instanceof TimeSeriesI) {
            TimeSeriesI parsedTS = (TimeSeriesI) parsed;
            TimeSeriesI origTS = (TimeSeriesI) value;
            assertEquals(origTS.getDegree(), parsedTS.getDegree());
            final double delta = 1.0e-12;
            assertArrayEquals(origTS.getTimes(), parsedTS.getTimes(), delta);
            assertArrayEquals(origTS.getValues(), parsedTS.getValues(), delta);
        } else {
            assertEquals(value, parsed);
        }        
        assertEquals(expression, Type.DYNAMIC.format(value, es));

        assertTrue(Type.DYNAMIC.isCompatible(value));
        assertTrue(Type.DYNAMIC.isCompatible(parsed));
    }
}
