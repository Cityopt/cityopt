package eu.cityopt.opt.ga.adapter;

import java.io.OutputStream;
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
import org.opt4j.core.start.Opt4JTask;

import com.google.inject.Module;

import eu.cityopt.opt.ga.CityoptPhenotype;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.opt.AlgorithmStatus;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.OptimisationResults;
import eu.cityopt.sim.opt.Solution;

/**
 * Provides sim-eval optimisation functionality on top of an Opt4J optimisation
 * algorithm. Specifically, provides a CompletableFuture that allows cancelling
 * the optimisation task, and converts the results to an OptimisationResults
 * instance.
 *
 * @author Hannu Rummukainen
 */
public class OptimiserAdapter {
    OptimisationProblem problem;
    Opt4JTask task;
    Control control;
    Archive archive;

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

    public OptimiserAdapter(
            OptimisationProblem problem, SimulationStorage storage,
            OutputStream messageSink, Instant deadline,
            Module... modules) {
        this.problem = problem;
        this.constraintAndObjectiveIndices = mapConstraintAndObjectiveIndices(problem);

        List<Module> moduleList = new ArrayList<>(Arrays.asList(modules));
        moduleList.add(new CityoptAdapterModule(problem, storage));
        this.task = new Opt4JTask(false);
        task.init(moduleList);

        task.open();
        this.control = task.getInstance(Control.class);
        this.archive = task.getInstance(Archive.class);

        //TODO use messageSink with Optimizer.addOptimizerIterationListener
        // maybe also addIndividualStateListener for slow simulations
    }

    public CompletableFuture<OptimisationResults> start(Executor executor) {
        executor.execute(() -> {
            try {
                OptimisationResults results = new OptimisationResults();
                try {
                    task.call();
                    //TODO implement timeout
                    if (control.getState() == Control.State.TERMINATED) {
                        results.status = AlgorithmStatus.INTERRUPTED;
                    } else {
                        results.status = AlgorithmStatus.COMPLETED_RESULTS;
                    }
                } catch (Throwable t) {
                    results.status = AlgorithmStatus.FAILED;
                }
                results.paretoFront = archive.stream().map(this::makeSolutionFromIndividual)
                        .filter(s -> s != null).collect(Collectors.toList());
                completableFuture.complete(results);
            } catch (Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        });
        return completableFuture;
    }

    private static Map<String, Integer> mapConstraintAndObjectiveIndices(
            OptimisationProblem problem) {
        Map<String, Integer> map = new HashMap<>();
        int nConstraints = problem.constraints.size();
        for (int i = 0; i < nConstraints; ++i) {
            Constraint constraint = problem.constraints.get(i);
            Integer old = map.put(constraint.getName(), i);
            if (old != null) {
                throw new IllegalArgumentException(
                        "Duplicate constraints with name " + constraint.getName());
            }
        }
        for (int i = 0; i < problem.objectives.size(); ++i) {
            ObjectiveExpression objective = problem.objectives.get(i);
            Integer old = map.put(objective.getName(), i);
            if (old != null) {
                if (old < nConstraints) {
                    throw new IllegalArgumentException(
                            "The same name " + objective.getName()
                            + " was used for both a constraint and an objective");
                } else {
                    throw new IllegalArgumentException(
                            "Duplicate objectives with name " + objective.getName());
                }
            }
        }
        return map;
    }

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
