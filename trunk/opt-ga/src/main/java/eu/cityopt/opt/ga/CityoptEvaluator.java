package eu.cityopt.opt.ga;

import javax.inject.Inject;

import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;

import eu.cityopt.sim.eval.SimulationInput;

public class CityoptEvaluator implements Evaluator<SimulationInput> {
    private OptimisationProblem problem;
    
    @Inject
    public CityoptEvaluator(OptimisationProblem problem) {
        this.problem = problem;
    }

    @Override
    public Objectives evaluate(SimulationInput phenotype) {
        // TODO Auto-generated method stub
        return null;
    }
}
