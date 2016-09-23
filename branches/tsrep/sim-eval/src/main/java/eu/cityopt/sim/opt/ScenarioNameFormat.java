package eu.cityopt.sim.opt;

import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;

/** Creates human-readable names for generated scenarios. */
public interface ScenarioNameFormat {
    /**
     * Returns array containing first a brief scenario name and then a longer
     * description.  Must be thread safe.
     */
    String[] format(DecisionValues decisions, SimulationInput input);

    /**
     * Variant of {@link #format(DecisionValues, SimulationInput)} to be used
     * when simulation input is not yet available.
     */
    String[] format(DecisionValues decisions);

    /**
     * Creates an extended description after evaluation results are available.
     */
	String extendDescription(String initialDescription,
			ConstraintStatus constraints, ObjectiveStatus objectives);
}
