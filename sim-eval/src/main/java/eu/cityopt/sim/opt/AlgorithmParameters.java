package eu.cityopt.sim.opt;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import eu.cityopt.sim.eval.ConfigurationException;

/**
 * Properties wrapper with support for typed values and defaults.
 * Provides accessors for the most common simulation optimisation
 * algorithm parameters.
 *
 * @author Hannu Rummukainen
 */
public class AlgorithmParameters {
    public static final String KEY_MAX_RUNTIME_MINUTES = "max runtime [minutes]";
    public static final Double DEFAULT_MAX_RUNTIME_MINUTES = (double) TimeUnit.DAYS.toMinutes(366);

    public static final String KEY_MAX_PARALLEL_EVALUATIONS = "max parallel evaluations";
    public static final int DEFAULT_MAX_PARALLEL_EVALUATIONS = 100;

    private Properties properties;

    /** Creates an empty object. */
    public AlgorithmParameters() {
        properties = new Properties();
    }

    public double getDouble(String key) throws ConfigurationException {
        return getDouble(key, null);
    }

    public double getDouble(String key, Double defaultValue)
            throws ConfigurationException {
        return (Double) parseProperty(key, defaultValue, Double::valueOf, "a decimal number");
    }

    public long getLong(String key) throws ConfigurationException {
        return getLong(key, null);
    }

    public long getLong(String key, Long defaultValue)
            throws ConfigurationException {
        return (Long) parseProperty(key, defaultValue, Long::valueOf, "an integer");
    }

    public int getInt(String key) throws ConfigurationException {
        return getInt(key, null);
    }

    public int getInt(String key, Integer defaultValue)
            throws ConfigurationException {
        return (Integer) parseProperty(key, defaultValue, Integer::valueOf, "an integer");
    }

    public boolean getBoolean(String key) throws ConfigurationException {
        return getBoolean(key, null);
    }

    public boolean getBoolean(String key, Boolean defaultValue)
            throws ConfigurationException {
        return (Boolean) parseProperty(key, defaultValue,
                AlgorithmParameters::parseBoolean, "a boolean flag");
    }

    static private Boolean parseBoolean(String value) {
        if (value.equals("1") || value.equalsIgnoreCase("t")
                || value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equals("0") || value.equalsIgnoreCase("f")
                || value.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getString(String key) throws ConfigurationException {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue)
            throws ConfigurationException {
        return (String) parseProperty(key, defaultValue, s -> s, "a string");
    }

    /** Maximum algorithm run time. */
    public Duration getMaxRunTime() throws ConfigurationException {
        double minutes = getDouble(KEY_MAX_RUNTIME_MINUTES, DEFAULT_MAX_RUNTIME_MINUTES);
        return Duration.ofNanos((long) (minutes * 60.0e9));
    }

    /** Maximum number of simulations that may be evaluated in parallel. */
    public int getMaxParallelEvaluations() throws ConfigurationException {
        return getInt(KEY_MAX_PARALLEL_EVALUATIONS,
                DEFAULT_MAX_PARALLEL_EVALUATIONS);
    }

    Object parseProperty(String key, Object defaultValue,
            Function<String, Object> parser, String what)
                    throws ConfigurationException {
        String value = properties.getProperty(mangleKey(key));
        if (value == null) {
            if (defaultValue == null) {
                throw new ConfigurationException(
                        "Parameter " + key + " is not defined.");
            } else {
                return defaultValue;
            }
        }
        try {
            return parser.apply(value);
        } catch (RuntimeException e) {
            throw new ConfigurationException(
                    "Parameter " + key + " value is not " + what + ": " + value);
        }
    }

    /** Returns the value of the named parameter as a string with no conversions. */
    public String getProperty(String key) {
        return properties.getProperty(mangleKey(key));
    }

    /** Whether the named parameter has a value. */
    public boolean containsKey(String key) {
        return properties.containsKey(mangleKey(key));
    }

    /**
     * Loads parameter values from an input stream in the Java property
     * file format (i.e. rows of the form "parameter = value"), where
     * spaces in parameter names are replaced with underscore characters.
     *
     * @see Properties#load(InputStream)
     */
    public void load(InputStream stream) throws IOException {
        properties.load(stream);
    }

    /** Sets the value of a named parameter to an unparsed string value. */
    public void put(String key, String value) {
        properties.put(mangleKey(key), value);
    }

    String mangleKey(String key) {
        return key.replace(' ', '_');
    }
}
