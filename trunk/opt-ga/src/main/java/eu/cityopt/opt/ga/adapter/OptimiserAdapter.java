package eu.cityopt.opt.ga.adapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.start.Opt4JTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.util.ExceptionHelpers;
import eu.cityopt.sim.opt.AlgorithmStatus;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.OptimisationResults;
import eu.cityopt.sim.opt.OptimisationStateListener;
import eu.cityopt.sim.opt.ScenarioNameFormat;
import eu.cityopt.sim.opt.SequentialScenarioNameFormat;

/**
 * Provides sim-eval optimisation functionality on top of an Opt4J optimisation
 * algorithm. Specifically, provides a CompletableFuture that allows cancelling
 * the optimisation task, and converts the results to an OptimisationResults
 * instance.
 *<p>
 * Known issue:  If the run is terminated during an iteration, the Pareto front
 * will not contain solutions discovered on the terminated iteration.  This is
 * because Opt4J updates the archive only at the end of an iteration.
 *
 * @author Hannu Rummukainen
 */
class OptimiserAdapter {
    private final Logger logger = LoggerFactory.getLogger(OptimiserAdapter.class);
    private final SolutionTransformer solxform;
    private final OptimisationStateListener listener;
    private final String runName;
    private final Opt4JTask task;
    private final TimeoutControl control;
    private final Archive archive;

    CompletableFuture<OptimisationResults>
    completableFuture = new CompletableFuture<OptimisationResults>() {
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (isCancelled()) {
                return true;
            }
            if (!mayInterruptIfRunning) {
                return false;
            }
            if (!super.cancel(mayInterruptIfRunning)) {
                return false;
            }
            control.doTerminate();
            return true;
        }
    };

    OptimiserAdapter(
            OptimisationProblem problem, SimulationStorage storage, String runName,
            OptimisationStateListener listener, Instant deadline, Module... modules) {
        solxform = new SolutionTransformer(problem);
        this.runName = runName;
        this.control = new TimeoutControl(deadline);
        this.listener = listener;

        ScenarioNameFormat formatter = new SequentialScenarioNameFormat(runName, problem.decisionVars);

        List<Module> moduleList = new ArrayList<>(Arrays.asList(modules));
        moduleList.add(new CityoptAdapterModule(problem, storage, formatter, listener, control));
        this.task = new Opt4JTask(false);
        task.init(moduleList);

        task.open();
        this.archive = task.getInstance(Archive.class);

        Optimizer optimizer = task.getInstance(Optimizer.class);
        optimizer.addOptimizerIterationListener(
                iteration -> listener.setProgressState("Iteration " + iteration));
    }

    CompletableFuture<OptimisationResults> start(Executor executor) {
        executor.execute(() -> {
            try {
                logger.info("Starting run " + runName);
                OptimisationResults results = new OptimisationResults();
                try {
                    task.call();
                    if (control.timeout) {
                        results.status = AlgorithmStatus.COMPLETED_TIME;
                    } else if (control.getState() == Control.State.TERMINATED) {
                        results.status = AlgorithmStatus.INTERRUPTED;
                    } else {
                        results.status = AlgorithmStatus.COMPLETED_RESULTS;
                    }
                } catch (Throwable t) {
                    results.status = AlgorithmStatus.FAILED;
                    logger.debug("Caught throwable from Opt4J", t);
                    t = ExceptionHelpers.peelCommonWrappers(t);
                    listener.logMessage("Terminating optimisation at error: " + t.getMessage());
                }
                results.paretoFront = archive.stream()
                        .map(solxform::makeSolutionFromIndividual)
                        .filter(s -> s.objectiveStatus != null)
                        .collect(Collectors.toList());
                completableFuture.complete(results);
            } catch (Throwable t) {
                completableFuture.completeExceptionally(t);
            }
            logger.info("Ending run " + runName);
        });
        return completableFuture;
    }
}
