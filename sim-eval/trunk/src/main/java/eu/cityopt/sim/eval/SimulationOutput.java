package eu.cityopt.sim.eval;

/**
 * Represents deterministic simulation output for specific input data.
 * 
 * The subclass SimulationResults is used when the simulation successfully
 * produces result data, and the subclass SimulationFailure is used in cases
 * where repeating the simulation with the same input would repeatably fail.
 * 
 * In case of an ephemeral simulation failure due to e.g. insufficient disk
 * space, an exception should be thrown instead of creating SimulationOutput.
 * 
 * @author Hannu Rummukainen
 */
public class SimulationOutput {
    private SimulationInput input;
    private String messages;

    protected SimulationOutput(SimulationInput input, String messages) {
        //FIXME Isn't it rather too late to worry about this?
        if (!input.isComplete()) {
            throw new IllegalArgumentException("Incomplete input");
        }
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
