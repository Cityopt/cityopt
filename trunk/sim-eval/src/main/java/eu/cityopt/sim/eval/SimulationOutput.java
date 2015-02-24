package eu.cityopt.sim.eval;

/**
 * Represents simulation output for specific input data.
 * 
 * The subclass SimulationResults is used when the simulation successfully
 * produces result data, and the subclass SimulationFailure is used in
 * failure cases.
 * 
 * @author Hannu Rummukainen
 */
public class SimulationOutput {
    private SimulationInput input;
    private String messages;

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
