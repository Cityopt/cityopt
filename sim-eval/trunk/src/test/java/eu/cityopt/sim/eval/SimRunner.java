package eu.cityopt.sim.eval;

import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulationStorage;

public class SimRunner implements SimulationRunner {
	@Override
	public SimJob start(SimulationInput input) {
		return new SimJob(input);
	}
}
