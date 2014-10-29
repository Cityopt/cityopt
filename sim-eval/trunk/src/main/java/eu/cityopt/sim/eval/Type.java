package eu.cityopt.sim.eval;

public enum Type {
    DOUBLE("Double") {
        public Object parse(String value) {
            return Double.parseDouble(value);
        }

        public boolean isInstance(Object value) {
            return value instanceof Double;
        }

        public String format(Object value) {
            return Double.toString((Double) value);
        }
    },

    INTEGER("Integer") {
        public Object parse(String value) {
            return Integer.parseInt(value);
        }

        public boolean isInstance(Object value) {
            return value instanceof Integer;
        }

        public String format(Object value) {
            return Integer.toString((Integer) value);
        }
    },

    STRING("String") {
        public Object parse(String value) {
            return value;
        }

        public boolean isInstance(Object value) {
            return value instanceof String;
        }

        public String format(Object value) {
            return (String) value;
        }
    },

    TIMESERIES("TimeSeries") {
        public Object parse(String value) {
            throw new UnsupportedOperationException(
                    "Cannot parse a time series");
        }

        public boolean isInstance(Object value) {
            return value instanceof TimeSeries;
        }

        public String format(Object value) {
            throw new UnsupportedOperationException(
                    "Cannot store a time series as a string");
        }
    };

    abstract public Object parse(String value);

    abstract public boolean isInstance(Object value);

    abstract public String format(Object value);

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
