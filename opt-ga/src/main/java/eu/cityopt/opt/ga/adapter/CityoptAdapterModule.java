package eu.cityopt.opt.ga.adapter;

import org.opt4j.core.optimizer.Control;

import com.google.inject.AbstractModule;

import eu.cityopt.opt.ga.CityoptModule;
import eu.cityopt.sim.opt.OptimisationLog;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.ScenarioNameFormat;
import eu.cityopt.sim.eval.SimulationStorage;

/**
 * Configures the classes required by {@link CityoptModule}.
 * Used by {@link OptimiserAdapter}.
 *
 * @author Hannu Rummukainen
 */
public class CityoptAdapterModule extends AbstractModule {
    private OptimisationProblem problem;
    private SimulationStorage storage;
    private ScenarioNameFormat formatter;
    private OptimisationLog optimisationLog;
    private Control control;

    /**
     * Provides the instances to be used by {@link CityoptModule}.
     * The {@link Control} argument can be left null.
     */
    public CityoptAdapterModule(
            OptimisationProblem problem, SimulationStorage storage,
            ScenarioNameFormat formatter, OptimisationLog optimisationLog,
            Control control) {
        this.problem = problem;
        this.storage = storage;
        this.formatter = formatter;
        this.optimisationLog = optimisationLog;
        this.control = control;
    }

    @Override
    protected void configure() {
        install(new CityoptModule());
        bind(OptimisationProblem.class).toInstance(problem);
        bind(SimulationStorage.class).toInstance(storage);
        bind(ScenarioNameFormat.class).toInstance(formatter);
        bind(OptimisationLog.class).toInstance(optimisationLog);
        if (control != null) {
            bind(Control.class).toInstance(control);
        }
    }
}
