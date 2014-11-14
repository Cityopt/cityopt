package eu.cityopt.sim.eval;

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
        public Object parse(String value) {
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
        public Object parse(String value) {
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
        public Object parse(String value) {
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
        public Object parse(String value) {
            throw new UnsupportedOperationException(
                    "Cannot parse a time series");
        }

        @Override
        public boolean isInstance(Object value) {
            return (value instanceof TimeSeries)
                    && ((TimeSeries) value).getDegree() == 0;
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
        public Object parse(String value) {
            throw new UnsupportedOperationException(
                    "Cannot parse a time series");
        }

        @Override
        public boolean isInstance(Object value) {
            return (value instanceof TimeSeries)
                    && ((TimeSeries) value).getDegree() == 1;
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
    };

    /** Constructs an object of this type, given a string representation of a value. */
    abstract public Object parse(String value);

    /** Determines whether the given object is of this particular type. */
    abstract public boolean isInstance(Object value);

    /** Formats an object of this type into its string representation. */
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
}
