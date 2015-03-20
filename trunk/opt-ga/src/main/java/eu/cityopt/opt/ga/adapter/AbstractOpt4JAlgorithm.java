package eu.cityopt.opt.ga.adapter;

import java.io.IOException;
import java.io.OutputStream;
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
import eu.cityopt.sim.opt.OptimisationResults;

public abstract class AbstractOpt4JAlgorithm implements OptimisationAlgorithm {
    @Override
    public CompletableFuture<OptimisationResults> start(
            eu.cityopt.sim.opt.OptimisationProblem problem,
            AlgorithmParameters parameters, SimulationStorage storage,
            OutputStream messageSink, Executor executor)
            throws ConfigurationException, IOException, ConfigurationException {
        Instant deadline = Instant.now().plus(parameters.getMaxRunTime());
        OptimiserAdapter adapter = new OptimiserAdapter(
                problem, storage, messageSink, deadline,
                configureOptimizer(parameters),
                configureRandom(parameters),
                configureIndividualCompleter(parameters),
                configureArchive(parameters));
        return adapter.start(executor);
    }

    protected abstract OptimizerModule configureOptimizer(
            AlgorithmParameters parameters) throws ConfigurationException;

    protected RandomModule configureRandom(
            AlgorithmParameters parameters) throws ConfigurationException {
        RandomModule rm = new RandomModule();
        String key = "seed of the random number generator";
        if (parameters.containsKey(key)) {
            rm.setSeed(parameters.getLong(key));
            rm.setUsingSeed(true);
        } else {
            rm.setUsingSeed(false);
        }
        return rm;
    }

    protected IndividualCompleterModule configureIndividualCompleter(
            AlgorithmParameters parameters) throws ConfigurationException {
        IndividualCompleterModule im = new IndividualCompleterModule();
        im.setType(Type.PARALLEL);
        im.setThreads(parameters.getMaxParallelEvaluations());
        return im;
    }

    protected ArchiveModule configureArchive(
            AlgorithmParameters parameters) throws ConfigurationException {
        ArchiveModule am = new ArchiveModule();
        am.setType(ArchiveModule.Type.UNBOUNDED);
        return am;
    }
}
