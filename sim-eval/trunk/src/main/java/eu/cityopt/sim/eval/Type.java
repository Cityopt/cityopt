package eu.cityopt.sim.eval;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Indicates the type of a named value.
 *
 * @see Namespace
 *
 * @author Hannu Rummukainen
 */
public enum Type {
    /** Double precision floating point */
    DOUBLE("Double") {
        @Override
        public Double parse(String value) {
            return Double.parseDouble(value);
        }

        @Override
        public boolean isInstance(Object value) {
            return value instanceof Double;
        }

        @Override
        public String format(Object value) {
            return Double.toString((Double) value);
        }
    },

    /** 32-bit signed integer */
    INTEGER("Integer") {
        @Override
        public Integer parse(String value) {
            return Integer.parseInt(value);
        }

        @Override
        public boolean isInstance(Object value) {
            return value instanceof Integer;
        }

        @Override
        public String format(Object value) {
            return Integer.toString((Integer) value);
        }
    },

    /** Unicode string */
    STRING("String") {
        @Override
        public String parse(String value) {
            return value;
        }

        @Override
        public boolean isInstance(Object value) {
            return value instanceof String;
        }

        @Override
        public String format(Object value) {
            return (String) value;
        }
    },

    /** Time series treated as a step function with values of type double. */
    TIMESERIES_STEP("TimeSeries/Step") {
        @Override
        public TimeSeries parse(String value) {
            throw new UnsupportedOperationException(
                    "Cannot parse a time series");
        }

        @Override
        public boolean isInstance(Object value) {
            return (value instanceof TimeSeriesI)
                    && ((TimeSeriesI) value).getDegree() == 0;
        }

        @Override
        public String format(Object value) {
            throw new UnsupportedOperationException(
                    "Cannot store a time series as a string");
        }

        @Override
        public int getInterpolationDegree() {
            return 0;
        }
    },

    /** Time series treated as a piecewise linear function with values of type double. */
    TIMESERIES_LINEAR("TimeSeries/Linear") {
        @Override
        public TimeSeries parse(String value) {
            throw new UnsupportedOperationException(
                    "Cannot parse a time series");
        }

        @Override
        public boolean isInstance(Object value) {
            return (value instanceof TimeSeriesI)
                    && ((TimeSeriesI) value).getDegree() == 1;
        }

        @Override
        public String format(Object value) {
            throw new UnsupportedOperationException(
                    "Cannot store a time series as a string");
        }

        @Override
        public int getInterpolationDegree() {
            return 1;
        }
    },

    LIST_OF_INTEGER("List of Integer") {
        @Override
        public List<Integer> parse(String value) throws ParseException {
            try {
                int[] array = objectMapper.readValue(value, int[].class);
                List<Integer> list = new ArrayList<Integer>(array.length);
                for (int i : array) {
                    list.add(i);
                }
                return list;
            } catch (IOException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isInstance(Object value) {
            if ( ! (value instanceof List)) {
                return false;
            } else {
                for (Object element : (List) value) {
                    if ( ! (element instanceof Integer)) {
                        return false;
                    }
                }
                return true;
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public String format(Object value) {
            try {
                return objectWriter.writeValueAsString((List) value);
            } catch (JsonProcessingException e) {
                throw new ClassCastException(
                        "Not a formattable list of integers: " + value);
            }
        }
    },

    LIST_OF_DOUBLE("List of Double") {
        @Override
        public List<Double> parse(String value) throws ParseException {
            try {
                double[] array = objectMapper.readValue(value, double[].class);
                List<Double> list = new ArrayList<Double>(array.length);
                for (double d : array) {
                    list.add(d);
                }
                return list;
            } catch (IOException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isInstance(Object value) {
            if ( ! (value instanceof List)) {
                return false;
            } else {
                for (Object element : (List) value) {
                    if ( ! (element instanceof Double)) {
                        return false;
                    }
                }
                return true;
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public String format(Object value) {
            try {
                return objectWriter.writeValueAsString((List) value);
            } catch (JsonProcessingException e) {
                throw new ClassCastException(
                        "Not a formattable list of doubles: " + value);
            }
        }
    };

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

    /**
     * Constructs an object of this type, given a string representation of a value.
     * @throws ParseException if the string cannot be parsed as the correct type
     */
    abstract public Object parse(String value) throws ParseException;

    /** Determines whether the given object is of this particular type. */
    abstract public boolean isInstance(Object value);

    /**
     * Formats an object of this type into its string representation.
     * @throws ClassCastException if the object is of some other type 
     */
    abstract public String format(Object value);

    /**
     * Returns the degree of time series interpolation.
     * @throws IllegalArgumentException if this is not a time series type.
     */
    public int getInterpolationDegree() {
        throw new IllegalArgumentException(name + " is not a time series type.");
    }

    /** The human-readable name of the type. */
    public final String name;

    Type(String name) {
        this.name = name;
    }

    /**
     * Gets a type by name.
     * 
     * @param name
     *            type name. Case insensitive.
     * @throws IllegalArgumentException
     *             if the type name is unknown
     */
    public static Type getByName(String name) {
        for (Type type : Type.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type \"" + name + "\"");
    }

    /**
     * Determines the type of a literal value.
     *
     * @param value a string representing an object of one of the defined types
     * @return 
     * @throws ParseException if the 
     */
    public static Type getFromValue(String value) throws ParseException {
        Object object;
        try {
            object = objectMapper.readValue(value, Object.class);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        for (Type type : values()) {
            if (type.isInstance(object)) {
                return type;
            }
        }
        throw new ParseException("Unsupported literal: " + value, 0);
    }
}
