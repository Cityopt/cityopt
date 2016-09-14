package eu.cityopt.sim.eval;

/**
 * Thrown when simulation model parsing fails if it is possible that the
 * model may be for a different simulator.
 *
 * If this is thrown during simulator auto-detection, another simulator
 * should be tried.  Other exceptions should usually be rethrown,
 * terminating auto-detection.
 * @author Timo Korvola
 */
@SuppressWarnings("serial")
public class AlienModelException extends ConfigurationException {
    public AlienModelException(String message) {
        super(message);
    }

    public AlienModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
