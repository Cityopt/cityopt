package eu.cityopt.opt.ga;

import org.opt4j.core.config.annotations.Ignore;
import org.opt4j.core.problem.ProblemModule;

import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.ScenarioNameFormat;

/**
 * Configure Opt4J for Cityopt.
 * To complete the problem definition a binding for {@link OptimisationProblem}
 * must be added separately.  Bindings for {@link SimulationStorage} and
 * {@link ScenarioNameFormat} may also be added; default is no storage.
 * To execute an optimisation task more bindings are needed, at minimum
 * an optimisation algorithm.
 *
 * @author ttekth
 */
@Ignore
public class CityoptModule extends ProblemModule {
    /**
     * Use DoubleMapGenotype instead of ComponentwiseGenotype.
     * Allows more algorithms but no integer variables.
     */
    public boolean realOnly = false;

    @Override
    protected void config() {
        addOptimizerStateListener(CityoptEvaluator.class);
        if (realOnly) {
            bindProblem(RealCreator.class, RealDecoder.class,
                        CityoptEvaluator.class);
        } else {
            bindProblem(ComponentwiseCreator.class, ComponentwiseDecoder.class,
                        CityoptEvaluator.class);
        }
    }
}
