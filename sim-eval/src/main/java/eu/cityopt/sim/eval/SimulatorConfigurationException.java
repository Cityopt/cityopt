package eu.cityopt.sim.eval;

@SuppressWarnings("serial")
public class SimulatorConfigurationException extends Exception {
    public SimulatorConfigurationException(String message) {
        super(message);
    }

    public SimulatorConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
