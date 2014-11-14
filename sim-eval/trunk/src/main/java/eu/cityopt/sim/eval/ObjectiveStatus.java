package eu.cityopt.sim.eval;

import java.util.Collection;

import javax.script.ScriptException;

/**
 * Container for objective value(s) computed from a single simulation run. Can
 * be used in both single-objective and multi-objective problems.
 *
 * @author Hannu Rummukainen
 */
public class ObjectiveStatus implements PartiallyComparable<ObjectiveStatus> {
    private final Namespace namespace;

    /**
     * Values of objective functions, in the same order as the constructor
     * arguments.
     */
    public final double[] objectiveValues;

    /**
     * Values of objective functions, with signs flipped so that smaller values
     * are preferred.
     */
    public final double[] asMinGoalValues;

    public ObjectiveStatus(MetricValues context,
            ObjectiveExpression singleObjective) throws ScriptException,
            InvalidValueException {
        this.namespace = context.getResults().getNamespace();
        double value = singleObjective.evaluate(context);
        this.objectiveValues = new double[] { value };
        this.asMinGoalValues = new double[] { singleObjective
                .flipSignIfMax(value) };
    }

    public ObjectiveStatus(MetricValues context,
            Collection<ObjectiveExpression> objectives) throws ScriptException,
            InvalidValueException {
        this.namespace = context.getResults().getNamespace();
        this.objectiveValues = new double[objectives.size()];
        this.asMinGoalValues = new double[objectives.size()];
        int i = 0;
        for (ObjectiveExpression objective : objectives) {
            double value = objective.evaluate(context);
            this.objectiveValues[i] = value;
            this.asMinGoalValues[i] = objective.flipSignIfMax(value);
            ++i;
        }
    }

    public ObjectiveStatus(ConstraintStatus constraintStatus,
            ObjectiveStatus objectiveStatus) {
        this.namespace = objectiveStatus.namespace;
        this.objectiveValues = constraintStatus.infeasibilities;
        this.asMinGoalValues = constraintStatus.infeasibilities;
    }

    /**
     * Determines if this solution dominates the other, or vice versa.
     * 
     * @param other
     *            another evaluation, should be based on the same objectives
     * @return negative if this dominates the other, positive if the other
     *         dominates this, zero if equal, and null if neither dominates.
     *         Null is not possible in single-objective problems.
     */
    public Integer compareTo(ObjectiveStatus other) {
        if (namespace != other.namespace) {
            throw new IllegalArgumentException(
                    "Cannot compare between different namespaces");
        }
        return PartialComparisons.compare(asMinGoalValues,
                other.asMinGoalValues);
    }

    public static Integer compare(ObjectiveStatus a, ObjectiveStatus b) {
        return a.compareTo(b);
    }
}
