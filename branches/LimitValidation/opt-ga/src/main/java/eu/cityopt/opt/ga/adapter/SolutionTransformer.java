package eu.cityopt.opt.ga.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Value;

import eu.cityopt.opt.ga.CityoptEvaluator;
import eu.cityopt.opt.ga.CityoptPhenotype;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.Solution;

/**
 * Transform Opt4J Individuals to sim-eval Solutions.
 */
public class SolutionTransformer {
    private final OptimisationProblem problem;
    /**
     * Map from constraint/objective name to its index in the problem definition,
     * represented as follows.  Suppose C is the number of constraints and B is
     * the number of objectives.  Then indices from 0 to (C-1) represent
     * constraints, and indices from C to C+B-1 represent objectives.
     */
    private Map<String, Integer> constraintAndObjectiveIndices;

    private Map<String, Integer> mapConstraintAndObjectiveIndices() {
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

    @Inject
    public SolutionTransformer(OptimisationProblem problem) {
        this.problem = problem;
        constraintAndObjectiveIndices
                = mapConstraintAndObjectiveIndices();
    }

    /**
     * Converts an Opt4J Individual to a sim-eval Solution.
     */
    public Solution makeSolutionFromIndividual(Individual individual) {
        CityoptPhenotype phenotype = (CityoptPhenotype) individual.getPhenotype();
        Objectives objectives = individual.getObjectives();
        int nConstraints = problem.constraints.size();
        double[] infeasibilities = new double[nConstraints];
        double[] objectiveValues = new double[problem.objectives.size()];
        for (Map.Entry<Objective, Value<?>> bar : objectives) {
            String name = bar.getKey().getName();
            Double valueOrNull = bar.getValue().getDouble();
            //XXX C.f. CityoptEvaluator#toObjectives.
            double value = (valueOrNull != null) ? valueOrNull : Double.NaN;
            int i = constraintAndObjectiveIndices.get(name);
            if (i < nConstraints) {
                infeasibilities[i] = value;
            } else {
                objectiveValues[i - nConstraints] = value;
            }
        }
        ConstraintStatus constraintStatus =
                new ConstraintStatus(problem.constraints, infeasibilities);
        ObjectiveStatus objectiveStatus
                = constraintStatus.isDefinitelyFeasible()
                ? new ObjectiveStatus(
                        problem.getNamespace(), objectiveValues,
                        problem.objectives)
                : null;
        return new Solution(
                constraintStatus, objectiveStatus, phenotype.input);
    }
}