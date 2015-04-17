package eu.cityopt.opt.ga.adapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Value;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.start.Opt4JTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

import eu.cityopt.opt.ga.CityoptEvaluator;
import eu.cityopt.opt.ga.CityoptPhenotype;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.util.ExceptionHelpers;
import eu.cityopt.sim.opt.AlgorithmStatus;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.OptimisationResults;
import eu.cityopt.sim.opt.OptimisationStateListener;
import eu.cityopt.sim.opt.ScenarioNameFormat;
import eu.cityopt.sim.opt.SimpleScenarioNameFormat;
import eu.cityopt.sim.opt.Solution;

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
    private final OptimisationProblem problem;
    private final OptimisationStateListener listener;
    private final String runName;
    private final Opt4JTask task;
    private final TimeoutControl control;
    private final Archive archive;

    /**
     * Map from constraint/objective name to its index in the problem definition,
     * represented as follows.  Suppose C is the number of constraints and B is
     * the number of objectives.  Then indices from 0 to (C-1) represent
     * constraints, and indices from C to C+B-1 represent objectives.
     */
    Map<String, Integer> constraintAndObjectiveIndices;

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
        this.problem = problem;
        this.runName = runName;
        this.constraintAndObjectiveIndices = mapConstraintAndObjectiveIndices(problem);
        this.control = new TimeoutControl(deadline);
        this.listener = listener;

        ScenarioNameFormat formatter = new SimpleScenarioNameFormat(runName, problem.decisionVars);

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
                results.paretoFront = archive.stream().map(this::makeSolutionFromIndividual)
                        .filter(s -> s != null).collect(Collectors.toList());
                completableFuture.complete(results);
            } catch (Throwable t) {
                completableFuture.completeExceptionally(t);
            }
            logger.info("Ending run " + runName);
        });
        return completableFuture;
    }

    private static Map<String, Integer> mapConstraintAndObjectiveIndices(
            OptimisationProblem problem) {
        Map<String, Integer> map = new HashMap<>();
        int nConstraints = problem.constraints.size();
        for (int i = 0; i < nConstraints; ++i) {
            Constraint constraint = problem.constraints.get(i);
            Integer old = map.put(CityoptEvaluator.getOName(constraint), i);
            if (old != null) {
                throw new IllegalArgumentException(
                        "Duplicate constraints with name " + constraint.getName());
            }
        }
        for (int i = 0; i < problem.objectives.size(); ++i) {
            ObjectiveExpression objective = problem.objectives.get(i);
            Integer old = map.put(CityoptEvaluator.getOName(objective), i + nConstraints);
            if (old != null) {
                throw new IllegalArgumentException(
                        "Duplicate objectives with name " + objective.getName());
            }
        }
        return map;
    }

    /**
     * Converts an Opt4J Individual to a feasible sim-eval Solution, or null if
     * the individual is not feasible.
     */
    Solution makeSolutionFromIndividual(Individual individual) {
        CityoptPhenotype phenotype = (CityoptPhenotype) individual.getPhenotype();
        Objectives objectives = individual.getObjectives();
        int nConstraints = problem.constraints.size();
        double[] infeasibilities = new double[nConstraints];
        double[] objectiveValues = new double[problem.objectives.size()];
        for (Map.Entry<Objective, Value<?>> bar : objectives) {
            String name = bar.getKey().getName();
            Double valueOrNull = bar.getValue().getDouble();
            double value = (valueOrNull != null) ? valueOrNull : Double.NaN;
            int i = constraintAndObjectiveIndices.get(name);
            if (i < nConstraints) {
                infeasibilities[i] = value;
            } else {
                objectiveValues[i - nConstraints] = value;
            }
        }
        ConstraintStatus constraintStatus = new ConstraintStatus(infeasibilities);
        if (constraintStatus.isDefinitelyFeasible()) {
            ObjectiveStatus objectiveStatus = new ObjectiveStatus(
                    problem.getNamespace(), objectiveValues, problem.objectives);
            return new Solution(constraintStatus, objectiveStatus, phenotype.input);
        } else {
            return null;
        }
    }
}
