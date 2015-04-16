package eu.cityopt.opt.ga.adapter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.opt4j.core.common.archive.ArchiveModule;
import org.opt4j.core.common.completer.IndividualCompleterModule;
import org.opt4j.core.common.completer.IndividualCompleterModule.Type;
import org.opt4j.core.common.random.RandomModule;
import org.opt4j.core.optimizer.OptimizerModule;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.opt.AlgorithmParameters;
import eu.cityopt.sim.opt.OptimisationAlgorithm;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.OptimisationResults;
import eu.cityopt.sim.opt.OptimisationStateListener;

/**
 * Skeleton implementation of the sim-eval OptimisationAlgorithm interface for
 * Opt4J algorithms. The start method configures the appropriate Opt4J modules
 * and passes them to OptimiserAdapter. Subclasses provide the Opt4J optimizer
 * module by implementing the configureOptimizer method.
 *
 * @author Hannu Rummukainen
 */
public abstract class AbstractOpt4JAlgorithm implements OptimisationAlgorithm {
    public static final String KEY_RANDOM_SEED = "seed of the random number generator";

    @Override
    public CompletableFuture<OptimisationResults> start(
            OptimisationProblem problem, AlgorithmParameters parameters,
            SimulationStorage storage, String runName,
            OptimisationStateListener listener, Instant deadline,
            Executor executor)
            throws ConfigurationException, IOException, ConfigurationException {
        OptimiserAdapter adapter = new OptimiserAdapter(
                problem, storage, runName, listener, deadline,
                configureOptimizer(parameters),
                configureRandom(parameters),
                configureIndividualCompleter(parameters),
                configureArchive(parameters));
        return adapter.start(executor);
    }

    /** Provides the Optimizer module. */
    protected abstract OptimizerModule configureOptimizer(
            AlgorithmParameters parameters) throws ConfigurationException;

    /**
     * Provides the Random module.  By default we use the Opt4J
     * RandomModule with a configurable random seed.
     */
    protected RandomModule configureRandom(
            AlgorithmParameters parameters) throws ConfigurationException {
        RandomModule rm = new RandomModule();
        if (parameters.containsKey(KEY_RANDOM_SEED)) {
            rm.setSeed(parameters.getLong(KEY_RANDOM_SEED));
            rm.setUsingSeed(true);
        } else {
            rm.setUsingSeed(false);
        }
        return rm;
    }

    /**
     * Provides the IndividualCompleter module. By default we use
     * ParallelIndividualCompleter with a configurable number of maximum
     * threads.
     */
    protected IndividualCompleterModule configureIndividualCompleter(
            AlgorithmParameters parameters) throws ConfigurationException {
        IndividualCompleterModule im = new IndividualCompleterModule();
        im.setType(Type.PARALLEL);
        im.setThreads(parameters.getMaxParallelEvaluations());
        return im;
    }

    /**
     * Provides the Archive module. By default, we use an unbounded archive in
     * order to keep track of all pareto-optimal solutions, which are then
     * provided as the final optimisation results by OptimizerAdapter.
     */
    protected ArchiveModule configureArchive(
            AlgorithmParameters parameters) throws ConfigurationException {
        ArchiveModule am = new ArchiveModule();
        am.setType(ArchiveModule.Type.UNBOUNDED);
        return am;
    }
}
