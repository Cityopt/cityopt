package eu.cityopt.sim.eval;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
    private final SimulationRunner runner;
    private final SimulationStorage storage;
    private final Executor executor;

    public SimulationRunnerWithStorage(SimulationRunner runner,
            SimulationStorage storage, Executor executor) {
        this.runner = runner;
        this.storage = storage;
        this.executor = executor;
    }

    /**
     * Acquires the simulation output corresponding to the given input.
     * Uses stored output data if available; otherwise starts a new simulation.
     * 
     * @param input
     * @return a future that will provide the simulation output if it can be
     *         determined, or fail with an exception.
     * @throws IOException 
     */
    @Override
    public CompletableFuture<SimulationOutput> start(SimulationInput input)
            throws IOException {
        SimulationOutput oldOutput = storage.get(input);
        if (oldOutput != null) {
            return CompletableFuture.completedFuture(oldOutput);
        } else {
            CompletableFuture<SimulationOutput> simulation = runner.start(input);
            // Store the output once the simulation is completed.
            simulation.thenAcceptAsync(newOutput -> storage.put(newOutput), executor);
            return simulation;
        }
    }

    @Override
    public void close() throws IOException {}
}
