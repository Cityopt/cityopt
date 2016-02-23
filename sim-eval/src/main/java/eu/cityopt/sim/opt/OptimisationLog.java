package eu.cityopt.sim.opt;

import javax.script.ScriptException;

import eu.cityopt.sim.eval.SimulationFailure;

/**
 * Minimal logging interface for user-level messages.
 * Messages may be sent concurrently from multiple threads.
 *
 * @author Hannu Rummukainen
 */
public interface OptimisationLog {
    /**
     * Processes a log message. The message may be one or more lines, and may
     * or may not end with '\n'.
     */
    public void logMessage(String text);

    public default void logSimulationFailure(
            String[] scenarioNameAndDescription, SimulationFailure failure) {
        String name = scenarioNameAndDescription[0];
        logMessage("Simulation of scenario " + name + " failed: " + failure.reason
                + "\nScenario " + name + ": " + scenarioNameAndDescription[1]);
    }

    public default void logEvaluationFailure(
            String[] scenarioNameAndDescription, ScriptException exception) {
        String name = scenarioNameAndDescription[0];
        logMessage("Evaluation of scenario " + name + " failed: " + exception.getMessage()
                + "\nScenario " + name + ": " + scenarioNameAndDescription[1]);
    }
}
