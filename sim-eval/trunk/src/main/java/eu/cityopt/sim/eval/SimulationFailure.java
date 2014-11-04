package eu.cityopt.sim.eval;

/**
 * Repeatable simulation failure with specific input data. This class should not
 * be used if the failure is temporary, e.g. due to insufficient disk space; in
 * such cases an Exception is preferred.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class SimulationFailure extends SimulationOutput {
    SimulationFailure(SimulationInput input, String messages) {
        super(input, messages);
    }
}
