package eu.cityopt.sim.eval;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.script.ScriptException;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.cityopt.sim.eval.util.TimeUtils;

/**
 * Indicates the type of a named parameter or variable.
 * <p>
 * There are three kinds of representations for each type.
 * We provide conversion functions between them.
 * <ul>
 *  <li> 
 *   A <em>value object</em> is an ordinary Java object, for example a Double
 *   or a String. Value objects are what is stored in classes such as
 *   SimulationInput and SimulationOutput.  For each Type, there is a small
 *   set of compatible Java value types.  The Python scripting engine provides
 *   access to value objects from Python expressions.
 *  </li>
 *  <li>
 *   The <em>user text</em> representation is what a user can read and type in.
 *   For example, <code>2015-14-21</code> for a TIMESTAMP.
 *  </li>
 *  <li>
 *   The <em>Python code</em> representation is a valid Python expression
 *   whose result is convertible to a compatible Java value type.
 *   For example, <code>'Foo\nBar'</code> evaluates to a string, and
 *   <code>1.14</code> to a Python float, which is convertible to a
 *   Java Double.
 *  </li>
 * </ul>
 *
 * @see Namespace
 *
 * @author Hannu Rummukainen
 */
public enum Type {
    /**
     * 32-bit signed integer.
     * <br>Compatible value types: Integer, Short, Byte.
     * <br>User text representation: decimal integer.
     * <br>Python type: int.
     */
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

        @Override
        public String toExpression(Object value, EvaluationSetup setup) {
            return Integer.toString((Integer) value);
        }

        @Override
        public Object fromScriptResult(Object value, EvaluationSetup setup)
                throws ScriptException {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                throw new ScriptException("Cannot convert to " + name);
            }
        }
    },

    /**
     * Double precision floating point number.
     * <br>Compatible value types: Double, Long, Integer, Short, Byte.
     * <br>User text representation: decimal number.
     * <br>Python type: float.
     */
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

        @Override
        public String toExpression(Object value, EvaluationSetup setup) {
            return setup.evaluator.toExpression((Double) value);
        }

        @Override
        public Object fromScriptResult(Object value, EvaluationSetup setup)
                throws ScriptException {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else {
                throw new ScriptException("Cannot convert to " + name);
            }
        }
    },

    /**
     * String.
     * <br>Compatible value types: String.
     * <br>User text representation: the string as such (i.e. there is no quoting).
     * <br>Python type: str.
     */
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

        @Override
        public String toExpression(Object value, EvaluationSetup setup) {
            return setup.evaluator.toExpression((String) value);
        }

        @Override
        public Object fromScriptResult(Object value, EvaluationSetup setup) {
            return value.toString();
        }
    },

    /**
     * Time stamp.
     * <br>Compatible value types: Double, Long, Integer, Short, Byte.
     * The value object is in simulation time, i.e. seconds from the time
     * origin of the simulation model.
     * <br>User text representation: ISO-8601 formatted string.  On input,
     * a decimal number representing simulation time is also allowed.
     * <br>Python type: float, in simulation time.  ISO-8601 formatted
     * strings and datetime objects are convertible to value objects.
     *
     */
    TIMESTAMP("Timestamp") {
        @Override
        public Double parse(String value, EvaluationSetup setup) throws ParseException {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException nfe) {
                value = value.trim();
                // Since timestamps must be quoted in LIST_OF_TIMESTAMP,
                // we don't mind quoted values here either.
                if (value.length() > 2 && value.charAt(0) == '"'
                        && value.charAt(value.length() - 1) == '"') {
                    value = value.substring(1, value.length() - 1); 
                }
                try {
                    Instant i = TimeUtils.parseISO8601(value);
                    return TimeUtils.toSimTime(i, setup.timeOrigin);
                } catch (DateTimeParseException dtpe) {
                    throw new ParseException(dtpe.getMessage(), dtpe.getErrorIndex());
                }
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

        @Override
        public String toExpression(Object value, EvaluationSetup setup) {
            return setup.evaluator.toExpression((Double) value);
        }

        @Override
        public Object fromScriptResult(Object value, EvaluationSetup setup)
                throws ScriptException {
            if (isCompatible(value)) {
                return value;
            }
            Instant t = null;
            if (value instanceof Date) {
                // Jython 2.5 converts datetime objects to java.sql.Timestamp
                t = ((Date) value).toInstant();
            } else if (value instanceof String) {
                try {
                    t = TimeUtils.parseISO8601((String) value);
                } catch (DateTimeParseException e) {
                    throw new ScriptException(e);
                }
            } else {
                throw new ScriptException("Not a valid " + name + ": " + value);
            }
            return TimeUtils.toSimTime(t, setup.timeOrigin);
        }
    },

    /**
     * Time series treated as a step function with values of type double.
     * <br>Compatible value types: TimeSeriesI with getDegree() equal to 0.
     * <br>User text representation: not supported.
     * <br>Python type: TimeSeries from the cityopt module
     */
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
        public String toExpression(Object value, EvaluationSetup setup) {
            return ((TimeSeriesI) value).__repr__();
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

    /**
     * Time series treated as a piecewise linear function with values of type double.
     * <br>Compatible value types: TimeSeriesI.
     * <br>User text representation: not supported. 
     * <br>Python type: TimeSeries from the cityopt module
     */
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
        public String toExpression(Object value, EvaluationSetup setup) {
            return ((TimeSeriesI) value).__repr__();
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
     * List of integers.
     * <br>Compatible value types: List<Integer>.
     * <br>User text representation: a bracketed comma-separated list of
     * decimal integers.
     * <br>Python type: list of int. 
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

        @Override
        public boolean isCompatible(Object value) {
            return Type.<Integer>isCompatibleList(value, INTEGER::isCompatible);
        }

        @SuppressWarnings("unchecked")
        @Override
        public String format(Object value, EvaluationSetup setup) {
            return bracketedList((List<Integer>) value,
                    i -> INTEGER.format(i, setup));
        }

        @SuppressWarnings("unchecked")
        @Override
        public String toExpression(Object value, EvaluationSetup setup) {
            return bracketedList((List<Integer>) value,
                    i -> INTEGER.toExpression(i, setup));
        }
    },

    /**
     * List of double precision floating point numbers.
     * <br>Compatible value types: List<Double>.
     * <br>User text representation: a bracketed comma-separated list of
     * decimal numbers.
     * <br>Python type: list of float or other numerical types.
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

        @Override
        public boolean isCompatible(Object value) {
            return Type.<Double>isCompatibleList(value, DOUBLE::isCompatible);
        }

        @SuppressWarnings("unchecked")
        @Override
        public String format(Object value, EvaluationSetup setup) {
            return bracketedList((List<Double>) value, 
                    d -> DOUBLE.format(d, setup));
        }

        @SuppressWarnings("unchecked")
        @Override
        public String toExpression(Object value, EvaluationSetup setup) {
            return bracketedList((List<Double>) value,
                    d -> DOUBLE.toExpression(d, setup));
        }
    },

    /**
     * List of time stamps.
     * <br>Compatible value types: List<Double>. The values are in simulation
     * time, i.e. seconds from the time origin of the simulation model.
     * <br>User text representation: a bracketed comma-separated list of quoted
     * ISO-8601 time stamps, or decimal numbers representing simulation time.
     * <br>Python type: list of float, or other numerical types, or 
     * ISO-8601 formatted strings, or datetime objects.
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
            } catch (IOException e) {
                throw new ParseException(e.getMessage(), 0);
            } catch (DateTimeParseException e) {
                throw new ParseException(e.getMessage(), e.getErrorIndex());
            }
        }

        @Override
        public boolean isCompatible(Object value) {
            return Type.<Double>isCompatibleList(value, TIMESTAMP::isCompatible);
        }

        @SuppressWarnings("unchecked")
        @Override
        public String format(Object value, EvaluationSetup setup) {
            return bracketedList((List<Double>) value,
                    t -> "\"" + TIMESTAMP.format(t, setup) + "\"");
        }

        @SuppressWarnings("unchecked")
        public String toExpression(Object value, EvaluationSetup setup) {
            return bracketedList((List<Double>) value,
                    t -> TIMESTAMP.toExpression(t,  setup));
        }

        @Override
        public Object fromScriptResult(Object value, EvaluationSetup setup)
                throws ScriptException {
            return fromScriptResultList(value, TIMESTAMP, setup);
        }
    };

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs a value object from a user text representation.
     * @throws ParseException if the string cannot be parsed as the correct type
     */
    abstract public Object parse(String value, EvaluationSetup setup) throws ParseException;

    /**
     * Converts a value object into the user text representation.
     * @throws ClassCastException if the object of an incompatible type
     */
    abstract public String format(Object value, EvaluationSetup setup);

    /** Returns whether the argument is of a compatible value type. */
    abstract public boolean isCompatible(Object value);

    /**
     * Converts a value object to a Python code representation.
     * @throws ClassCastException if the object of an incompatible type
     */
    abstract public String toExpression(Object value, EvaluationSetup setup);

    /** 
     * Constructs a value object by evaluating the Python code representation.
     * @throws ScriptException if evaluation or conversion to value object fails 
     */
    public Object evalExpression(String expression, EvaluationSetup setup)
            throws ScriptException {
        return fromScriptResult(setup.evaluator.eval(expression, setup), setup);
    }

    /**
     * Converts the result of a Python expression to a compatible value type,
     * if possible.
     * @throws ScriptException if conversion fails.
     */
    public Object fromScriptResult(Object value, EvaluationSetup setup) throws ScriptException {
        if (isCompatible(value)) {
            return value;
        } else {
            throw new ScriptException("Cannot convert to " + name());
        }
    }

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

    @SuppressWarnings("rawtypes")
    protected static <T> boolean isCompatibleList(
            Object value, Predicate<Object> checkElement) {
        if ( ! (value instanceof List)) {
            return false;
        } else {
            for (Object element : (List) value) {
                if ( ! checkElement.test(element)) {
                    return false;
                }
            }
            return true;
        }
    }

    protected <T> String bracketedList(List<T> list, Function<T, String> formatElement) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean delimit = false;
        for (T element : list) {
            if (delimit) {
                sb.append(", ");
            } else {
                delimit = true;
            }
            sb.append(formatElement.apply(element));
        }
        sb.append(']');
        return sb.toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> List<T> fromScriptResultList(
            Object value, Type elementType, EvaluationSetup setup) throws ScriptException {
        if ( ! (value instanceof List)) {
            throw new ScriptException("Not a " + name + ": value");
        } else {
            List<T> result = new ArrayList<T>();
            for (Object element : (List) value) {
                result.add((T) elementType.fromScriptResult(element, setup));
            }
            return result;
        }
    }
}
