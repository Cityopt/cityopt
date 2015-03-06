package eu.cityopt.sim.opt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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

public abstract class AbstractOptimisationJob implements Runnable {
    protected final OptimisationProblem problem;
    protected final SimulationRunner runner;
    protected final SimulationStorage storage;
    protected final OutputStream messageSink;
    protected final Executor executor;

    protected CompletableFuture<OptimisationResults> completableFuture =
            new CompletableFuture<>();

    /**
     * Read by the default isFeasible() method.
     * Set via the evaluateAsync method.
     */
    protected volatile boolean problemFeasible;

    public AbstractOptimisationJob(OptimisationProblem problem,
            SimulationStorage storage, OutputStream messageSink,
            Executor executor) throws IOException, ConfigurationException {
        this.problem = problem;
        this.storage = storage;
        this.messageSink = messageSink;
        this.executor = executor;
        this.runner = problem.makeRunner();
    }

    @Override
    public void run() {
        OptimisationResults result = new OptimisationResults();
        try {
            result.status = doRun();
        } catch (InterruptedException e) {
            result.status = AlgorithmStatus.INTERRUPTED;
        } catch (Throwable t) {
            result.status = AlgorithmStatus.FAILED;
        }
        try {
            result.paretoFront = getParetoFront();
            result.feasible = isFeasible();
        } catch (Throwable t) {
            result.status = AlgorithmStatus.FAILED;
        }
        completableFuture.complete(result);
    }

    abstract protected AlgorithmStatus doRun() throws Exception;

    abstract protected Collection<Solution> getParetoFront() throws Exception;

    protected boolean isFeasible() {
        return problemFeasible;
    }

    protected CompletableFuture<Solution> evaluateAsync(
            DecisionValues decisions) throws ScriptException, IOException {
        SimulationInput input = new SimulationInput(problem.inputConst);
        input.putExpressionValues(decisions, problem.inputExprs);

        ConstraintContext preConstraintContext = new ConstraintContext(decisions, input);
        ConstraintStatus preConstraintStatus =
                new ConstraintStatus(preConstraintContext, problem.constraints, true);
        if (preConstraintStatus.mayBeFeasible()) {
            CompletableFuture<SimulationOutput> job = runner.start(input);
            return job.thenApplyAsync(output -> {
                try {
                    storage.put(output);
                    if (output instanceof SimulationResults) {
                        SimulationResults results = (SimulationResults) output;
                        MetricValues metricValues = new MetricValues(results, problem.metrics);
                        storage.updateMetricValues(metricValues);

                        ConstraintContext postConstraintContext =
                                new ConstraintContext(preConstraintContext, metricValues);
                        ConstraintStatus postConstraintStatus =
                                new ConstraintStatus(postConstraintContext, problem.constraints, false);
                        if (postConstraintStatus.isDefinitelyFeasible()) {
                            problemFeasible = true;
                        }

                        ObjectiveStatus objectiveStatus =
                                new ObjectiveStatus(metricValues, problem.objectives);
                        return new Solution(postConstraintStatus, objectiveStatus, metricValues);
                    } else {
                        return null;
                    }
                } catch (ScriptException e) {
                    return null;
                }
            }, executor);
        } else {
            return CompletableFuture.completedFuture(
                    new Solution(preConstraintStatus, null, null));
        }
    }

    List<Solution> updateParetoFront(List<Solution> front, Solution newSolution) {
        if (newSolution != null && newSolution.metricValues != null
                && newSolution.constraintStatus.isDefinitelyFeasible()) {
            Iterator<Solution> it = front.iterator();
            while (it.hasNext()) {
                Solution oldSolution = it.next();
                Integer cmp = oldSolution.compareTo(newSolution);
                if (cmp != null) {
                    if (cmp < 0) {
                        // oldSolution dominates newSolution
                        return front;
                    } else if (cmp > 0) {
                        // newSolution dominates oldSolution
                        it.remove();
                    }
                }
            }
            front.add(newSolution);
        }
        return front;
    }
}
