package eu.cityopt.sim.eval;

/**
 * An error in simulator configuration, or unknown simulator name.
 *
 * @author Hannu Rummukainen
 */
@SuppressWarnings("serial")
public class SimulatorConfigurationException extends Exception {
    public SimulatorConfigurationException(String message) {
        super(message);
    }

    public SimulatorConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
