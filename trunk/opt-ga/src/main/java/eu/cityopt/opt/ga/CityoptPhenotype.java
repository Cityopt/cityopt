package eu.cityopt.opt.ga;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.SimulationInput;

public class CityoptPhenotype {
    public final DecisionValues decisions;
    public final SimulationInput input;
    
    public CityoptPhenotype(DecisionValues decisions, SimulationInput input) {
        this.decisions = decisions;
        this.input = input;
    }

    @Override
    public String toString() {
        return String.format("Phenotype(%s)", decisions);
    }
}
