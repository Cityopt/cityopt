package eu.cityopt.sim.eval;

import java.time.Instant;

/**
 * Represents simulation output for specific input data.
 * 
 * The subclass SimulationResults is used when the simulation successfully
 * produces result data, and the subclass SimulationFailure is used in
 * failure cases.
 * 
 * @author Hannu Rummukainen
 */
public abstract class SimulationOutput {
    private final SimulationInput input;
    /** Human-readable messages from simulator, or null. */
    public final String messages;
    /** Simulation run start time, or null. */
    public Instant runStart;
    /** Simulation run end time, or null. */
    public Instant runEnd;

    protected SimulationOutput(SimulationInput input, String messages) {
        this.input = input;
        this.messages = messages;
    }

    public SimulationInput getInput() {
        return input;
    }

    public String getMessages() {
        return messages;
    }
}
