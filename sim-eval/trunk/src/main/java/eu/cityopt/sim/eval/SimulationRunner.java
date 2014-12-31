package eu.cityopt.sim.eval;

import java.io.Closeable;
import java.util.concurrent.Future;

/**
 * Interface for starting new simulation jobs.
 *
 * @author Hannu Rummukainen
 */
public interface SimulationRunner extends Closeable {
    /**
     * Starts a simulation with the given input.
     * Do not modify the SimulationInput object afterward.
     *
     * @param input
     * @return a future that will provide the simulation output if it can be
     *         determined, or otherwise fail with an exception.
     */
    public Future<SimulationOutput> start(SimulationInput input);
}
