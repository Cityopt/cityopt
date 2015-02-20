package eu.cityopt.sim.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestType {
    EvaluationSetup es = new EvaluationSetup(null, Instant.ofEpochMilli(1420070400000L));

    @SuppressWarnings("unchecked")
    @Test
    public void parseListOfInteger() throws Exception {
        assertEquals(Arrays.asList(1, 2, 3), Type.LIST_OF_INTEGER.parse("[1, 2, 3]", es));
        assertEquals("[ 1, 2, 3 ]", Type.LIST_OF_INTEGER.format(Arrays.asList(1, 2, 3), es));
        assertEquals(Arrays.asList(), Type.LIST_OF_INTEGER.parse("[]", es));
        assertEquals("[ ]", Type.LIST_OF_INTEGER.format(Arrays.asList(), es));
        assertEquals(Arrays.asList(1, 2, 3), Type.LIST_OF_INTEGER.parse("[1.0, 2.0, 3.0]", es));
        assertTrue(Type.LIST_OF_INTEGER.isCompatible(Type.LIST_OF_INTEGER.parse("[1, 2, 3]", es)));
        assertTrue(Type.LIST_OF_INTEGER.isCompatible(Type.LIST_OF_INTEGER.parse("[1.0, 2.0, 3.0]", es)));
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
        assertEquals("[ 1.0, 2.0, 3.0 ]", Type.LIST_OF_DOUBLE.format(Arrays.asList(1.0, 2.0, 3.0), es));
        assertEquals(Arrays.asList(), Type.LIST_OF_DOUBLE.parse("[]", es));
        assertEquals("[ ]", Type.LIST_OF_DOUBLE.format(Arrays.asList(), es));
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), Type.LIST_OF_DOUBLE.parse("[1.0, 2.0, 3.0]", es));
        assertTrue(Type.LIST_OF_DOUBLE.isCompatible(Type.LIST_OF_DOUBLE.parse("[1, 2, 3]", es)));
        assertTrue(Type.LIST_OF_DOUBLE.isCompatible(Type.LIST_OF_DOUBLE.parse("[1.0, 2.0, 3.0]", es)));
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
        String TS123 = "[\"2015-01-01T00:00:01Z\", \"2015-01-01T00:00:02Z\", \"2015-01-01T00:00:03Z\"]";
        assertEquals(TS123, Type.LIST_OF_TIMESTAMP.format(Arrays.asList(1.0, 2.0, 3.0), es));
        assertEquals(Arrays.asList(), Type.LIST_OF_TIMESTAMP.parse("[]", es));
        assertEquals("[]", Type.LIST_OF_TIMESTAMP.format(Arrays.asList(), es));
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), Type.LIST_OF_TIMESTAMP.parse(TS123, es));
        assertTrue(Type.LIST_OF_TIMESTAMP.isCompatible(Type.LIST_OF_TIMESTAMP.parse("[1, 2, 3]", es)));
        assertTrue(Type.LIST_OF_TIMESTAMP.isCompatible(Type.LIST_OF_TIMESTAMP.parse("[1.0, 2.0, 3.0]", es)));
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
}
