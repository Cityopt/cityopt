package eu.cityopt.sim.eval;

/**
 * Combines constraint status and objective function status.
 * 
 * Constraint violations are compared first, and only if both solutions are
 * equally feasible, are objective functions compared.
 *<p>
 * Can be constructed from a pre-simulation constraint status, without any
 * objective status, as long as the constraint status is strictly infeasible.
 * 
 * @author Hannu Rummukainen
 */
public class CombinedObjectiveStatus implements
        PartiallyComparable<CombinedObjectiveStatus> {
    /**
     * Must be non-null and known feasible or infeasible.
     * Some individual constraints may be unevaluated.
     */
    public final ConstraintStatus constraintStatus;

    /** May be null. */
    public final ObjectiveStatus objectiveStatus;

    public CombinedObjectiveStatus(ConstraintStatus constraintStatus,
            ObjectiveStatus objectiveStatus) {
        this.constraintStatus = constraintStatus;
        if (constraintStatus.feasible == null) {
            throw new IllegalArgumentException("Feasibility unknown.");
        }
        this.objectiveStatus = objectiveStatus;
    }

    boolean isFeasible() {
        return constraintStatus.feasible;
    }

    @Override
    public Integer compareTo(CombinedObjectiveStatus other) {
        Integer cmp = constraintStatus.compareTo(other.constraintStatus);
        if (cmp != null && cmp == 0) {
            cmp = objectiveStatus.compareTo(other.objectiveStatus);
        }
        return cmp;
    }

    public static Integer compare(CombinedObjectiveStatus a,
            CombinedObjectiveStatus b) {
        return a.compareTo(b);
    }
}
