package eu.cityopt.opt.ga;

import org.opt4j.core.problem.ProblemModule;

public class CityoptModule extends ProblemModule {
    OptimisationProblem problem;
    
    public CityoptModule(OptimisationProblem problem) {
        this.problem = problem;
    }

    @Override
    protected void config() {
        bind(OptimisationProblem.class).toInstance(problem);
        //bindProblem(creator, decoder, evaluator);
    }
}
