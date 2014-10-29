package eu.cityopt.sim.eval;

/**
 * Combines constraint status and objective function status.
 *
 * Constraint violations are compared first, and only if both solutions
 * are equally feasible, are objective functions compared.
 *
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class CombinedObjectiveStatus implements PartiallyComparable<CombinedObjectiveStatus> {
	public final ConstraintStatus constraintStatus;
	public final ObjectiveStatus objectiveStatus;

	public CombinedObjectiveStatus(
			ConstraintStatus constraintStatus, ObjectiveStatus objectiveStatus) {
		this.constraintStatus = constraintStatus;
		this.objectiveStatus = objectiveStatus;
	}

	@Override
	public Integer compareTo(CombinedObjectiveStatus other) {
		Integer cmp = constraintStatus.compareTo(other.constraintStatus);
		if (cmp != null && cmp == 0) {
			cmp = objectiveStatus.compareTo(other.objectiveStatus);
		}
		return cmp;
	}

	public static Integer compare(CombinedObjectiveStatus a, CombinedObjectiveStatus b) {
		return a.compareTo(b);
	}
}
