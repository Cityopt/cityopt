package eu.cityopt.sim.eval;

import java.util.ArrayList;
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

    /** The evaluated objectives. */
    public final Collection<ObjectiveExpression> objectives;

    /**
     * Values of objective functions, in the same order as {@link #objectives}.
     */
    public final double[] objectiveValues;

    /**
     * Values of objective functions, with signs flipped so that smaller values
     * are preferred.
     */
    public final double[] asMinGoalValues;

    public ObjectiveStatus(MetricValues context,
            ObjectiveExpression singleObjective) throws ScriptException {
        this.namespace = context.getResults().getNamespace();
        this.objectives = new ArrayList<>();
        objectives.add(singleObjective);
        double value = singleObjective.evaluateDouble(context);
        this.objectiveValues = new double[] { value };
        this.asMinGoalValues = new double[] { singleObjective.flipSignIfMax(value) };
    }

    public ObjectiveStatus(MetricValues context,
            Collection<ObjectiveExpression> objectives) throws ScriptException {
        this.namespace = context.getResults().getNamespace();
        this.objectives = objectives;
        this.objectiveValues = new double[objectives.size()];
        this.asMinGoalValues = new double[objectives.size()];
        int i = 0;
        for (ObjectiveExpression objective : objectives) {
            double value = objective.evaluateDouble(context);
            this.objectiveValues[i] = value;
            this.asMinGoalValues[i] = objective.flipSignIfMax(value);
            ++i;
        }
    }

    public ObjectiveStatus(Namespace namespace, double[] objectiveValues,
            Collection<ObjectiveExpression> objectives) {
        this.namespace = namespace;
        this.objectives = objectives;
        this.objectiveValues = objectiveValues;
        this.asMinGoalValues = new double[objectiveValues.length];
        int i = 0;
        for (ObjectiveExpression objective : objectives) {
            this.asMinGoalValues[i] = objective.flipSignIfMax(objectiveValues[i]);
            ++i;
        }
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
