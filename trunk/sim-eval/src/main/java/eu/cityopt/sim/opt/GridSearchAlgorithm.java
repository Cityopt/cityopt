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
            SimulationStorage storage, OutputStream messageSink,
            Executor executor) throws ConfigurationException, IOException, ConfigurationException {
        GridSearchOptimiser job = new GridSearchOptimiser(
                problem, parameters, storage, messageSink, executor);
        executor.execute(job);
        return job.completableFuture;
    }
}
