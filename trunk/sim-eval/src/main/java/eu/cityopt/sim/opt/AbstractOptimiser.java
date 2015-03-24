package eu.cityopt.sim.opt;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.script.ScriptException;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.ConstraintContext;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulationStorage;

public abstract class AbstractOptimiser implements Runnable {
    protected final OptimisationProblem problem;
    protected final SimulationStorage storage;
    protected final ScenarioNameFormat formatter;
    protected final OutputStream messageSink;
    protected final Executor executor;
    protected SimulationRunner runner;

    protected CompletableFuture<OptimisationResults> completableFuture =
            new CompletableFuture<>();

    AtomicReferenceArray<CompletableFuture<SimulationOutput>> activeJobs;
    BlockingQueue<Integer> freeJobIds;
    Object jobsDoneCondition = new Object();
    long jobCounter = 1;

    Object paretoFrontMutex = new Object();
    ParetoFront paretoFront = new ParetoFront();

    public AbstractOptimiser(OptimisationProblem problem,
            SimulationStorage storage, String runName,
            OutputStream messageSink, Executor executor,
            int maxEvaluationsInParallel)
                    throws IOException, ConfigurationException {
        this.problem = problem;
        this.storage = storage;
        this.formatter = new SimpleScenarioNameFormat(runName, problem.decisionVars);
        this.messageSink = messageSink;
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
                result.status = AlgorithmStatus.FAILED;
            }
            try {
                cancel();
                result.paretoFront = takeParetoFront();
            } catch (Throwable t) {
                result.status = AlgorithmStatus.FAILED;
            }
        } finally {
            try {
                runner.close();
                runner = null;
            } catch (Throwable t) {
                System.err.println("Failed to clean up: " + t);
            }
            completableFuture.complete(result);
        }
    }

    abstract protected AlgorithmStatus doRun() throws Exception;

    protected void queueJob(DecisionValues decisions, int jobId)
            throws ScriptException, IOException {
        SimulationInput input = new SimulationInput(problem.inputConst);
        input.putExpressionValues(decisions, problem.inputExprs);

        ConstraintContext preConstraintContext = new ConstraintContext(decisions, input);
        ConstraintStatus preConstraintStatus =
                new ConstraintStatus(preConstraintContext, problem.constraints, true);
        if (preConstraintStatus.mayBeFeasible()) {
            final String jobName = "#" + jobCounter + " [" + jobId + "]";
            ++jobCounter;
            System.err.println("Starting simulation job " + jobName + ": " + decisions);
            CompletableFuture<SimulationOutput> job = runner.start(input);
            activeJobs.set(jobId, job);
            job.thenApplyAsync(output -> {
                try {
                    storage.put(output, formatter.format(decisions, input));
                    if (output instanceof SimulationResults) {
                        SimulationResults results = (SimulationResults) output;
                        MetricValues metricValues = new MetricValues(results, problem.metrics);
                        storage.updateMetricValues(metricValues);

                        ConstraintContext postConstraintContext =
                                new ConstraintContext(preConstraintContext, metricValues);
                        ConstraintStatus postConstraintStatus =
                                new ConstraintStatus(postConstraintContext, problem.constraints, false);

                        ObjectiveStatus objectiveStatus =
                                new ObjectiveStatus(metricValues, problem.objectives);
                        Solution solution = new Solution(
                                postConstraintStatus, objectiveStatus, input);
                        updateParetoFront(solution);
                        return solution;
                    } else {
                        return null;
                    }
                } catch (ScriptException e) {
                    return null;
                }
            }, executor)
            .whenComplete((solution, throwable) -> {
                activeJobs.set(jobId, null);
                freeJobIds.add(jobId);
                if (freeJobIds.remainingCapacity() == 0) {
                    synchronized (jobsDoneCondition) {
                        jobsDoneCondition.notifyAll();
                    }
                }
                System.err.println("Completed simulation job " + jobName);
            });
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
        System.err.println("Waiting for remaining simulation jobs to complete.");
        if (freeJobIds.remainingCapacity() > 0) {
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
        System.err.println("Canceling simulation jobs.");
        for (int i = 0; i < activeJobs.length(); ++i) {
            CompletableFuture<SimulationOutput> job = activeJobs.get(i);
            if (job != null && !job.isDone()) {
                job.cancel(true);
            }
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
