package eu.cityopt.sim.eval;

import java.util.concurrent.Future;

public interface SimulationRunner {
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
