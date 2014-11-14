package eu.cityopt.sim.eval;

import java.util.concurrent.Future;

/**
 * SimulationRunner that prefers to use SimulationStorage when possible,
 * and only runs new simulations when there is no stored simulation data
 * available.  New simulation results are updated in storage.
 * 
 * Note that this class neither computes nor stores any metrics.
 * 
 * @author Hannu Rummukainen
 */
public class SimulationRunnerWithStorage implements SimulationRunner {
    private SimulationRunner runner;
    private SimulationStorage storage;

    public SimulationRunnerWithStorage(SimulationRunner runner,
            SimulationStorage storage) {
        this.runner = runner;
        this.storage = storage;
    }

    /**
     * Acquires the simulation output corresponding to the given input.
     * Uses stored output data if available; otherwise starts a new simulation.
     * 
     * @param input
     * @return a future that will provide the simulation output if it can be
     *         determined, or fail with an exception.
     */
    @Override
    public Future<SimulationOutput> start(SimulationInput input) {
        SimulationOutput output = storage.get(input);
        if (output != null) {
            return new ImmediateFuture<SimulationOutput>(output);
        } else {
            Future<SimulationOutput> simulation = runner.start(input);
            // Store the output once the simulation is completed.
            return new FutureTransform<SimulationOutput, SimulationOutput>(
                    simulation) {
                public SimulationOutput transform(SimulationOutput output) {
                    storage.put(output);
                    return output;
                }
            };
        }
    }
}
