package eu.cityopt.opt.ga.adapter;

import com.google.inject.AbstractModule;

import eu.cityopt.opt.ga.CityoptModule;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.eval.SimulationStorage;

public class CityoptAdapterModule extends AbstractModule {
    SimulationStorage storage;
    OptimisationProblem problem;

    public CityoptAdapterModule(
            OptimisationProblem problem, SimulationStorage storage) {
        this.problem = problem;
        this.storage = storage;
    }

    @Override
    protected void configure() {
        install(new CityoptModule());
        bind(OptimisationProblem.class).toInstance(problem);
        bind(SimulationStorage.class).toInstance(storage);
    }

}
