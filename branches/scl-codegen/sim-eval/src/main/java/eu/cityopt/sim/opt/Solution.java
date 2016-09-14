package eu.cityopt.sim.opt;

import eu.cityopt.sim.eval.CombinedObjectiveStatus;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;

/**
 * An evaluated solution to a simulation optimisation problem.
 *<p>
 * @see OptimisationProblem
 *
 * @author Hannu Rummukainen
 */
public class Solution extends CombinedObjectiveStatus {
    public final SimulationInput input;

    public Solution(ConstraintStatus constraintStatus,
            ObjectiveStatus objectiveStatus, SimulationInput input) {
        super(constraintStatus, objectiveStatus);
        this.input = input;
    }
}
