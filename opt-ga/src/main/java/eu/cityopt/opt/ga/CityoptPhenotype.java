package eu.cityopt.opt.ga;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.SimulationInput;

public class CityoptPhenotype {
    public final DecisionValues decisions;
    public final SimulationInput input;
    public final String[] description;

    public CityoptPhenotype(DecisionValues decisions, SimulationInput input, String[] description) {
        this.decisions = decisions;
        this.input = input;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Phenotype(%s)", description[1]);
    }
}
