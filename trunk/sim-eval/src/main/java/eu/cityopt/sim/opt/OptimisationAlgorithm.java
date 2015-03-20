package eu.cityopt.sim.opt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.ConfigurationException;

/**
 * Interface for running an optimisation algorithm.
 *
 * @author Hannu Rummukainen
 */
public interface OptimisationAlgorithm {
    /**
     * Starts an optimisation algorithm asynchronously.
     *
     * @param problem specifies the decision variables, constraints etc.
     * @param parameters contains algorithm specific configuration
     * @param storage where to store simulation outputs and computed metrics
     * @param messageSink for writing user-readable progress messages
     * @param executor to be used for asynchronous execution of the algorithm.
     * @return future gives the final status and results of the algorithm.
     * @throws ConfigurationException 
     * @throws IOException 
     */
    CompletableFuture<OptimisationResults> start(
            OptimisationProblem problem, AlgorithmParameters parameters,
            SimulationStorage storage, OutputStream messageSink, Executor executor)
                    throws ConfigurationException, IOException, ConfigurationException;

    String getName();
}
