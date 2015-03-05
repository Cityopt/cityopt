package eu.cityopt.opt.ga;

import org.opt4j.core.config.annotations.Ignore;
import org.opt4j.core.problem.ProblemModule;

import eu.cityopt.sim.eval.SimulationStorage;

/**
 * Configure Opt4J for Cityopt.
 * To complete the problem definition bindings for {@link OptimisationProblem}
 * and optionally {@link SimulationStorage} must be added separately.
 * To execute an optimisation task more bindings are needed, at minimum
 * an optimisation algorithm.
 * 
 * @author ttekth
 */
@Ignore
public class CityoptModule extends ProblemModule {
    @Override
    protected void config() {
        addOptimizerStateListener(CityoptEvaluator.class);
        bindProblem(ComponentwiseCreator.class, ComponentwiseDecoder.class,
                    CityoptEvaluator.class);
    }
}
