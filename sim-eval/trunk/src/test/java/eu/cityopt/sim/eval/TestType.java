package eu.cityopt.sim.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestType {
    @SuppressWarnings("unchecked")
    @Test
    public void parseListOfInteger() throws Exception {
        assertEquals(Arrays.asList(1, 2, 3), Type.LIST_OF_INTEGER.parse("[1, 2, 3]"));
        assertEquals("[ 1, 2, 3 ]", Type.LIST_OF_INTEGER.format(Arrays.asList(1, 2, 3)));
        assertEquals(Arrays.asList(), Type.LIST_OF_INTEGER.parse("[]"));
        assertEquals("[ ]", Type.LIST_OF_INTEGER.format(Arrays.asList()));
        assertEquals(Arrays.asList(1, 2, 3), Type.LIST_OF_INTEGER.parse("[1.0, 2.0, 3.0]"));
        assertTrue(Type.LIST_OF_INTEGER.isInstance(Type.LIST_OF_INTEGER.parse("[1, 2, 3]")));
        assertTrue(Type.LIST_OF_INTEGER.isInstance(Type.LIST_OF_INTEGER.parse("[1.0, 2.0, 3.0]")));
        assertTrue(((List<Integer>)Type.LIST_OF_INTEGER.parse("[1.0, 2.0, 3.0]")).get(0)
                instanceof Integer);
    }

    @Test(expected=ParseException.class)
    public void parseListOfInteger_MixedList() throws Exception {
        System.out.println(Type.LIST_OF_INTEGER.parse("[1, \"2\", \"x\"]"));
    }

    @Test(expected=ParseException.class)
    public void parseListOfInteger_Scalar() throws Exception {
        System.out.println(Type.LIST_OF_INTEGER.parse("1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void parseListOfDouble() throws Exception {
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), Type.LIST_OF_DOUBLE.parse("[1, 2, 3]"));
        assertEquals("[ 1.0, 2.0, 3.0 ]", Type.LIST_OF_DOUBLE.format(Arrays.asList(1.0, 2.0, 3.0)));
        assertEquals(Arrays.asList(), Type.LIST_OF_DOUBLE.parse("[]"));
        assertEquals("[ ]", Type.LIST_OF_DOUBLE.format(Arrays.asList()));
        assertEquals(Arrays.asList(1.0, 2.0, 3.0), Type.LIST_OF_DOUBLE.parse("[1.0, 2.0, 3.0]"));
        assertTrue(Type.LIST_OF_DOUBLE.isInstance(Type.LIST_OF_DOUBLE.parse("[1, 2, 3]")));
        assertTrue(Type.LIST_OF_DOUBLE.isInstance(Type.LIST_OF_DOUBLE.parse("[1.0, 2.0, 3.0]")));
        assertTrue(((List<Double>)Type.LIST_OF_DOUBLE.parse("[1, 2, 3]")).get(0)
                instanceof Double);
    }

    @Test(expected=ParseException.class)
    public void parseListOfDouble_MixedList() throws Exception {
        System.out.println(Type.LIST_OF_DOUBLE.parse("[1, \"2\", \"x\"]"));
    }

    @Test(expected=ParseException.class)
    public void parseListOfDouble_Scalar() throws Exception {
        System.out.println(Type.LIST_OF_DOUBLE.parse("1.0"));
    }

    @Test
    public void getTypeFromValue() throws Exception {
        assertEquals(Type.DOUBLE, Type.getFromValue("1.0"));
        assertEquals(Type.INTEGER, Type.getFromValue("13"));
        assertEquals(Type.STRING, Type.getFromValue("\"foo\""));
        assertEquals(Type.LIST_OF_INTEGER, Type.getFromValue("[9]"));
        assertEquals(Type.LIST_OF_DOUBLE, Type.getFromValue("[2.0]"));
    }

    @Test(expected=ParseException.class)
    public void getTypeFromValue_Object() throws Exception {
        Type.getFromValue("{ \"id\" : 1 }");
    }

    @Test(expected=ParseException.class)
    public void getTypeFromValue_UnquotedName() throws Exception {
        Type.getFromValue("argh");
    }
}
