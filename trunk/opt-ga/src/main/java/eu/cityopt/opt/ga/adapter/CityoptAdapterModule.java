package eu.cityopt.opt.ga.adapter;

import org.opt4j.core.optimizer.Control;

import com.google.inject.AbstractModule;

import eu.cityopt.opt.ga.CityoptModule;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.eval.SimulationStorage;

/**
 * Configures the OptimisationProblem and SimulationStorage for CityoptModule.
 *
 * @author Hannu Rummukainen
 */
public class CityoptAdapterModule extends AbstractModule {
    SimulationStorage storage;
    OptimisationProblem problem;
    Control control;

    public CityoptAdapterModule(
            OptimisationProblem problem, SimulationStorage storage,
            Control control) {
        this.problem = problem;
        this.storage = storage;
        this.control = control;
    }

    @Override
    protected void configure() {
        install(new CityoptModule());
        bind(OptimisationProblem.class).toInstance(problem);
        bind(SimulationStorage.class).toInstance(storage);
        if (control != null) {
            bind(Control.class).toInstance(control);
        }
    }

}
