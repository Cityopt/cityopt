package eu.cityopt.sim.opt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.ConfigurationException;

public class GridSearchAlgorithm implements OptimisationAlgorithm {

    @Override
    public CompletableFuture<OptimisationResults> start(
            OptimisationProblem problem, AlgorithmParameters parameters,
            SimulationStorage storage, String runName,
            OutputStream messageSink, Executor executor)
                    throws ConfigurationException, IOException, ConfigurationException {
        GridSearchOptimiser job = new GridSearchOptimiser(
                problem, parameters, storage, runName, messageSink, executor);
        executor.execute(job);
        return job.completableFuture;
    }

    @Override
    public String getName() {
        return "grid search";
    }
}
