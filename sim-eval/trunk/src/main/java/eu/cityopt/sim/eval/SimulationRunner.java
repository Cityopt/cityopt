package eu.cityopt.sim.eval;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Interface for starting new simulation jobs.
 * The runner must be closed when no longer needed.  Closing invalidates
 * all jobs started by the runner; they must not be accessed after closing
 * the runner.  It is recommended that all jobs are waited for (with get)
 * or cancelled before closing the runner.
 *
 * @author Hannu Rummukainen
 */
public interface SimulationRunner extends Closeable {
    /**
     * Starts a simulation with the given input.
     * The input object must not be modified afterwards.
     *
     * @param input
     * @return a future that will provide the simulation output if it can be
     *         determined, or otherwise fail with an exception.
     * @throws IOException 
     */
    public Future<SimulationOutput> start(SimulationInput input)
            throws IOException;
}
