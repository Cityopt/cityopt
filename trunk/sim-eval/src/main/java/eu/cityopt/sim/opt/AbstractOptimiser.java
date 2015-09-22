package eu.cityopt.sim.opt;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.ConstraintContext;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.util.ExceptionHelpers;

/**
 * Algorithm-independent functionality for simulation optimisation:
 * Handles evaluation of simulations in parallel, given decision variable
 * values.  For now the class contains only as much functionality as needed
 * by the grid search algorithm.
 *
 * @author Hannu Rummukainen
 */
public abstract class AbstractOptimiser implements Runnable {
    protected final Logger logger = LoggerFactory.getLogger(AbstractOptimiser.class);
    protected final OptimisationProblem problem;
    protected final SimulationStorage storage;
    protected final String runName;
    protected final ScenarioNameFormat formatter;
    protected final OptimisationStateListener listener;
    protected final Executor executor;
    protected SimulationRunner runner;

    protected CompletableFuture<OptimisationResults> completableFuture =
            new CompletableFuture<>();

    AtomicReferenceArray<CompletableFuture<SimulationOutput>> activeJobs;
    AtomicInteger evaluationsCompleted = new AtomicInteger();
    BlockingQueue<Integer> freeJobIds;
    Object jobsDoneCondition = new Object();
    long jobCounter = 1;

    Object paretoFrontMutex = new Object();
    ParetoFront paretoFront = new ParetoFront();

    public AbstractOptimiser(OptimisationProblem problem,
            SimulationStorage storage, String runName,
            OptimisationStateListener listener, Executor executor,
            int maxEvaluationsInParallel)
                    throws IOException, ConfigurationException {
        this.problem = problem;
        this.storage = storage;
        this.runName = runName;
        this.formatter = new SequentialScenarioNameFormat(runName, problem.decisionVars);
        this.listener = listener;
        this.executor = executor;

        activeJobs = new AtomicReferenceArray<CompletableFuture<SimulationOutput>>(
                maxEvaluationsInParallel);
        freeJobIds = new ArrayBlockingQueue<>(maxEvaluationsInParallel);
        for (int i = 0; i < maxEvaluationsInParallel; ++i) {
            freeJobIds.add(i);
        }
        assert freeJobIds.remainingCapacity() == 0;
    }

    @Override
    public void run() {
        logger.info("Starting run " + runName);
        OptimisationResults result = new OptimisationResults();
        try {
            try {
                runner = problem.makeRunner();
                result.status = doRun();
            } catch (TimeoutException e) {
                result.status = AlgorithmStatus.COMPLETED_TIME;
            } catch (InterruptedException e) {
                result.status = AlgorithmStatus.INTERRUPTED;
            } catch (Throwable t) {
                logger.warn("Caught exception", t);
                t = ExceptionHelpers.peelCommonWrappers(t);
                listener.logMessage("Algorithm failed: " + t.getMessage());
                result.status = AlgorithmStatus.FAILED;
            }
            try {
                cancel();
                result.paretoFront = takeParetoFront();
            } catch (Throwable t) {
                logger.warn("Caught unexpected exception", t);
                result.status = AlgorithmStatus.FAILED;
            }
        } finally {
            try {
                runner.close();
                runner = null;
            } catch (Throwable t) {
                logger.warn("Failed to clean up", t);
            }
            completableFuture.complete(result);
            logger.info("Ending run " + runName);
        }
    }

    abstract protected AlgorithmStatus doRun() throws Exception;

    protected CompletableFuture<Solution> queueJob(DecisionValues decisions, int jobId)
            throws ScriptException, IOException {
        SimulationInput input = new SimulationInput(problem.inputConst);
        try {
            input.putExpressionValues(decisions, problem.inputExprs);
        } catch (ScriptException e) {
            listener.logEvaluationFailure(formatter.format(decisions), e);
            throw e;
        }

        ConstraintContext preConstraintContext = new ConstraintContext(decisions, input);
        ConstraintStatus preConstraintStatus =
                new ConstraintStatus(preConstraintContext, problem.constraints, true);
        if (preConstraintStatus.mayBeFeasible()) {
            final String jobName = "#" + jobCounter + " [" + jobId + "]";
            ++jobCounter;
            logger.info("Starting simulation job " + jobName + ": " + decisions);
            CompletableFuture<SimulationOutput> simulationJob = runner.start(input);
            activeJobs.set(jobId, simulationJob);
            String[] nameAndDescription = formatter.format(decisions, input);
            CompletableFuture<Solution> evaluationJob =
                    simulationJob.thenApplyAsync(output -> {
                SimulationStorage.Put put = new SimulationStorage.Put(input, nameAndDescription);
                put.decisions = decisions;
                put.output = output;
                try {
                    if (output instanceof SimulationResults) {
                        SimulationResults results = (SimulationResults) output;
                        put.metricValues = new MetricValues(results, problem.metrics);

                        ConstraintContext postConstraintContext = new ConstraintContext(
                                preConstraintContext, put.metricValues);
                        put.constraintStatus = new ConstraintStatus(
                                postConstraintContext, problem.constraints, false);

                        put.objectiveStatus =
                                new ObjectiveStatus(put.metricValues, problem.objectives);
                        Solution solution = new Solution(
                                put.constraintStatus, put.objectiveStatus, input);
                        updateParetoFront(solution);
                        return solution;
                    } else {
                        listener.logSimulationFailure(
                                nameAndDescription, (SimulationFailure) output);
                        return null;
                    }
                } catch (ScriptException e) {
                    listener.logEvaluationFailure(nameAndDescription, e);
                    return null;
                } finally {
                    if (put.output != null) {
                        put.description = formatter.extendDescription(
                    			put.description, put.constraintStatus, put.objectiveStatus);
                        storage.put(put);
                    }
                }
            }, executor).whenComplete((solution, throwable) -> {
                evaluationsCompleted.incrementAndGet();
            });
            evaluationJob.whenComplete((solution, throwable) -> {
                // Propagate cancelation of evaluationJob back to simulationJob.
                if (evaluationJob.isCancelled()) {
                    simulationJob.cancel(true);
                }
                activeJobs.set(jobId, null);
                freeJobIds.add(jobId);
                synchronized (jobsDoneCondition) {
                    jobsDoneCondition.notifyAll();
                }
                logger.info("Completed simulation job " + jobName);
            });
            return evaluationJob;
        } else {
            evaluationsCompleted.incrementAndGet();
            Solution solution = new Solution(preConstraintStatus, null, input);
            return CompletableFuture.completedFuture(solution);
        }
    }

    protected int getJobId(Instant deadline) throws InterruptedException, TimeoutException {
        long millisLeft = Instant.now().until(deadline, ChronoUnit.MILLIS);
        final Integer jobId = freeJobIds.poll(millisLeft, TimeUnit.MILLISECONDS);
        if (jobId == null) {
            throw new TimeoutException("Optimisation timeout");
        }
        return jobId;
    }

    protected void waitForCompletion(Instant deadline)
            throws InterruptedException, TimeoutException {
        if (freeJobIds.remainingCapacity() > 0) {
            logger.info("Waiting for remaining simulation jobs to complete.");
            synchronized (jobsDoneCondition) {
                while (freeJobIds.remainingCapacity() > 0) {
                    long millisLeft = Instant.now().until(deadline, ChronoUnit.MILLIS);
                    if (millisLeft <= 0) {
                        throw new TimeoutException("Optimisation timeout");
                    }
                    jobsDoneCondition.wait(millisLeft);
                }
            }
        }
    }

    protected void cancel() {
        int canceled = 0;
        for (int i = 0; i < activeJobs.length(); ++i) {
            CompletableFuture<SimulationOutput> job = activeJobs.get(i);
            if (job != null && !job.isDone()) {
                job.cancel(true);
                ++canceled;
            }
        }
        if (canceled > 0) {
            logger.info("Canceled " + canceled + " simulation jobs.");
        }
    }

    protected void updateParetoFront(Solution solution) {
        synchronized (paretoFrontMutex) {
            if (paretoFront != null) {
                paretoFront.add(solution);
            }
        }
    }

    protected Collection<Solution> takeParetoFront() {
        synchronized (paretoFrontMutex) {
            List<Solution> solutions = paretoFront.solutions;
            paretoFront = null;
            return solutions;
        }
    }
}
