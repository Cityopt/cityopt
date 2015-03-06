package eu.cityopt.sim.opt;

import eu.cityopt.sim.eval.CombinedObjectiveStatus;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.ObjectiveStatus;

public class Solution extends CombinedObjectiveStatus {
    public final MetricValues metricValues;

    public Solution(ConstraintStatus constraintStatus,
            ObjectiveStatus objectiveStatus, MetricValues metricValues) {
        super(constraintStatus, objectiveStatus);
        this.metricValues = metricValues;
    }
}
