package eu.cityopt.sim.eval;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import eu.cityopt.sim.eval.util.TimeUtils;

/**
 * Indicates the type of a named value.
 *
 * @see Namespace
 *
 * @author Hannu Rummukainen
 */
public enum Type {
    /** 32-bit signed integer */
    INTEGER("Integer") {
        @Override
        public Integer parse(String value, EvaluationSetup setup) {
            return Integer.parseInt(value);
        }

        @Override
        public boolean isCompatible(Object value) {
            return (value instanceof Integer
                    || value instanceof Short
                    || value instanceof Byte);
        }

        @Override
        public String format(Object value, EvaluationSetup setup) {
            return Integer.toString((Integer) value);
        }
    },

    /** Double precision floating point */
    DOUBLE("Double") {
        @Override
        public Double parse(String value, EvaluationSetup setup) {
            return Double.parseDouble(value);
        }

        @Override
        public boolean isCompatible(Object value) {
            return (value instanceof Number);
        }

        @Override
        public String format(Object value, EvaluationSetup setup) {
            return Double.toString((Double) value);
        }
    },

    /** Unicode string */
    STRING("String") {
        @Override
        public String parse(String value, EvaluationSetup setup) {
            return value;
        }

        @Override
        public boolean isCompatible(Object value) {
            return (value instanceof String);
        }

        @Override
        public String format(Object value, EvaluationSetup setup) {
            return (String) value;
        }
    },

    /**
     * Time stamp in ISO-8601 format.
     * The actual value object is a Double in simulation time.  
     */
    TIMESTAMP("Timestamp") {
        @Override
        public Double parse(String value, EvaluationSetup setup) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                Instant i = TimeUtils.parseISO8601(value);
                return TimeUtils.toSimTime(i, setup.timeOrigin);
            }
        }

        @Override
        public boolean isCompatible(Object value) {
            return (value instanceof Number);
        }

        @Override
        public String format(Object value, EvaluationSetup setup) {
            Instant i = TimeUtils.toInstant((Double) value, setup.timeOrigin);
            return TimeUtils.formatISO8601(i);
        }
    },

    /** Time series treated as a step function with values of type double. */
    TIMESERIES_STEP("TimeSeries/Step") {
        @Override
        public TimeSeries parse(String value, EvaluationSetup setup) {
            throw new UnsupportedOperationException(
                    "Cannot parse a time series");
        }

        @Override
        public boolean isCompatible(Object value) {
            return (value instanceof TimeSeriesI)
                    && ((TimeSeriesI) value).getDegree() == 0;
        }

        @Override
        public String format(Object value, EvaluationSetup setup) {
            throw new UnsupportedOperationException(
                    "Cannot store a time series as a string");
        }

        @Override
        public int getInterpolationDegree() {
            return 0;
        }

        @Override
        public boolean isTimeSeriesType() {
            return true;
        }
    },

    /** Time series treated as a piecewise linear function with values of type double. */
    TIMESERIES_LINEAR("TimeSeries/Linear") {
        @Override
        public TimeSeries parse(String value, EvaluationSetup setup) {
            throw new UnsupportedOperationException(
                    "Cannot parse a time series");
        }

        @Override
        public boolean isCompatible(Object value) {
            return (value instanceof TimeSeriesI);
        }

        @Override
        public String format(Object value, EvaluationSetup setup) {
            throw new UnsupportedOperationException(
                    "Cannot store a time series as a string");
        }

        @Override
        public int getInterpolationDegree() {
            return 1;
        }

        @Override
        public boolean isTimeSeriesType() {
            return true;
        }
    },

    /**
     * List of integers. Represented by List<Integer> in Java. The text
     * representation is a bracketed comma-separated list of integers.
     */
    LIST_OF_INTEGER("List of Integer") {
        @Override
        public List<Integer> parse(String value, EvaluationSetup setup) throws ParseException {
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
        public boolean isCompatible(Object value) {
            if ( ! (value instanceof List)) {
                return false;
            } else {
                for (Object element : (List) value) {
                    if ( ! INTEGER.isCompatible(element)) {
                        return false;
                    }
                }
                return true;
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public String format(Object value, EvaluationSetup setup) {
            try {
                return objectWriter.writeValueAsString((List) value);
            } catch (JsonProcessingException e) {
                throw new ClassCastException(
                        "Not a formattable list of integers: " + value);
            }
        }
    },

    /**
     * List of double precision floating point numbers. Represented by
     * List<Double> in Java. The text representation is a bracketed
     * comma-separated list of decimal numbers.
     */
    LIST_OF_DOUBLE("List of Double") {
        @Override
        public List<Double> parse(String value, EvaluationSetup setup) throws ParseException {
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
        public boolean isCompatible(Object value) {
            if ( ! (value instanceof List)) {
                return false;
            } else {
                for (Object element : (List) value) {
                    if ( ! (DOUBLE.isCompatible(element))) {
                        return false;
                    }
                }
                return true;
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public String format(Object value, EvaluationSetup setup) {
            try {
                return objectWriter.writeValueAsString((List) value);
            } catch (JsonProcessingException e) {
                throw new ClassCastException(
                        "Not a formattable list of doubles: " + value);
            }
        }
    },

    /**
     * List of time stamps. Represented by List<Double> in Java. The text
     * representation is a bracketed comma-separated list of quoted
     * ISO-8601 strings.
     */
    LIST_OF_TIMESTAMP("List of Timestamp") {
        @Override
        public List<Double> parse(String value, EvaluationSetup setup) throws ParseException {
            try {
                Object[] array = objectMapper.readValue(value, Object[].class);
                List<Double> list = new ArrayList<Double>(array.length);
                for (Object o : array) {
                    if (o instanceof Number) {
                        list.add(((Number) o).doubleValue());
                    } else if (o instanceof String) {
                        Instant i = TimeUtils.parseISO8601((String) o);
                        list.add(TimeUtils.toSimTime(i, setup.timeOrigin));
                    } else {
                        throw new ParseException("Not a valid timestamp: " + o, 0);
                    }
                }
                return list;
            } catch (IOException | DateTimeParseException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isCompatible(Object value) {
            if ( ! (value instanceof List)) {
                return false;
            } else {
                for (Object element : (List) value) {
                    if ( ! TIMESTAMP.isCompatible(element)) {
                        return false;
                    }
                }
                return true;
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public String format(Object value, EvaluationSetup setup) {
            if (! (value instanceof List)) {
                throw new ClassCastException(
                        "Not a formattable list of doubles: " + value);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                boolean delimit = false;
                for (Object element : (List) value) {
                    if (delimit) {
                        sb.append(", ");
                    } else {
                        delimit = true;
                    }
                    sb.append('"');
                    sb.append(TIMESTAMP.format(element, setup));
                    sb.append('"');
                }
                sb.append(']');
                return sb.toString();
            }
        }
    };

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

    /**
     * Constructs an object of this type, given a string representation of a value.
     * @throws ParseException if the string cannot be parsed as the correct type
     */
    abstract public Object parse(String value, EvaluationSetup setup) throws ParseException;

    /**
     * Returns whether the value if of a type that can be assigned to this type,
     * allowing for numerical conversions to types with larger range.  Floating
     * point values cannot be assigned to INTEGER.  All numerical values can be
     * assigned to DOUBLE.
     */
    abstract public boolean isCompatible(Object value);

    /**
     * Formats an object of this type into its string representation.
     * @throws ClassCastException if the object is of some other type 
     */
    abstract public String format(Object value, EvaluationSetup setup);

    /**
     * Returns the degree of time series interpolation.
     * @throws IllegalArgumentException if this is not a time series type.
     */
    public int getInterpolationDegree() {
        throw new IllegalArgumentException(name + " is not a time series type.");
    }

    /**
     * Whether this is a time series type. Time series instances must be
     * constructed by calling {@link Evaluator#makeTS(Type, double[], double[])},
     * whereas for other types you can call {@link Type#parse(String)}.
     */
    public boolean isTimeSeriesType() {
        return false;
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
     * Converts the object to one of the supported types if appropriate.
     * Basically normalizes numerical types to Integer or Double.
     */
    public static Object normalize(Object o) {
        if (o instanceof Float || o instanceof Double) {
            return ((Number) o).doubleValue();
        } else if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        return o;
    }
}
