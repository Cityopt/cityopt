package eu.cityopt.opt.ga;

import org.opt4j.core.problem.ProblemModule;

public class CityoptModule extends ProblemModule {
    @Override
    protected void config() {
        addOptimizerStateListener(CityoptEvaluator.class);
        bindProblem(ComponentwiseCreator.class, ComponentwiseDecoder.class,
                    CityoptEvaluator.class);
    }
}
