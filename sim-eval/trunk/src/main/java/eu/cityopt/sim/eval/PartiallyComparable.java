package eu.cityopt.sim.eval;

public interface PartiallyComparable<T> {
	/**
	 * Determines if this object dominates the other, or vice versa.
	 * @param other another evaluation, should be based on the same objectives
	 * @return negative if this dominates the other, positive if the other
	 * dominates this, zero if equal, and null if neither dominates.
	 */
	public Integer compareTo(T other);
}
