package eu.cityopt.sim.opt;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulationStorage;

/**
 * Starts the grid search algorithm.
 *
 * @see GridSearchOptimiser
 *
 * @author Hannu Rummukainen
 */
public class GridSearchAlgorithm implements OptimisationAlgorithm {

    @Override
    public CompletableFuture<OptimisationResults> start(
            OptimisationProblem problem, AlgorithmParameters parameters,
            SimulationStorage storage, String runName,
            OptimisationStateListener listener, Instant deadline,
            Executor executor)
                    throws ConfigurationException, IOException, ConfigurationException {
        GridSearchOptimiser job = new GridSearchOptimiser(
                problem, parameters, storage, runName, listener, deadline, executor);
        executor.execute(job);
        return job.completableFuture;
    }

    @Override
    public String getName() {
        return "grid search";
    }
}
