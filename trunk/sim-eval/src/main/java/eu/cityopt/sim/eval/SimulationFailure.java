package eu.cityopt.sim.eval;

/**
 * Simulation failure with specific input data.
 * 
 * @author Hannu Rummukainen
 */
public class SimulationFailure extends SimulationOutput {
    /**
     * Whether the failure is repeatable with the same input data. 
     * Otherwise the failure is temporary, e.g. due to insufficient disk space,
     * and another attempt may succeed.
     */
    public final boolean permanent;

    public SimulationFailure(SimulationInput input, boolean permanent, String messages) {
        super(input, messages);
        this.permanent = permanent;
    }
}
