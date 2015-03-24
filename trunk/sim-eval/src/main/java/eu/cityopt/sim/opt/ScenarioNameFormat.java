package eu.cityopt.sim.opt;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.SimulationInput;

/** Creates human-readable names for generated scenarios. */
public interface ScenarioNameFormat {
    /**
     * Returns array containing first a brief scenario name and then a longer
     * description.  Must be thread safe.
     */
    String[] format(DecisionValues decisions, SimulationInput input);
}
