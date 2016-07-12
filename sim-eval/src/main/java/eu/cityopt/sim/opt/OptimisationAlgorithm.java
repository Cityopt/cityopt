package eu.cityopt.sim.opt;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulationStorage;

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
     * @param runName short run name to be used in scenario names
     * @param listener for receiving progress information
     * @param deadline when the run should stop, by the latest
     * @param executor to be used for asynchronous execution of the algorithm.
     * @return future gives the final status and results of the algorithm.
     * @throws ConfigurationException 
     * @throws IOException 
     */
    CompletableFuture<OptimisationResults> start(
            OptimisationProblem problem, AlgorithmParameters parameters,
            SimulationStorage storage, String runName,
            OptimisationStateListener listener, Instant deadline,
            Executor executor)
                    throws ConfigurationException, IOException, ConfigurationException;

    /** Returns the user-visible name of the optimisation algorithm. */
    String getName();
}
