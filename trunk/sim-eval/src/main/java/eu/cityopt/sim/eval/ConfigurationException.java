package eu.cityopt.sim.eval;

/**
 * An error in configuration parameters, for example an unknown simulator name.
 *
 * @author Hannu Rummukainen
 */
@SuppressWarnings("serial")
public class ConfigurationException extends Exception {
    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
