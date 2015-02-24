package eu.cityopt.opt.ga;

import javax.inject.Inject;
import javax.script.ScriptException;

import org.opt4j.core.genotype.MapGenotype;
import org.opt4j.core.problem.Decoder;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.InvalidValueException;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.Type;

public class ComponentwiseDecoder
implements Decoder<ComponentwiseGenotype, SimulationInput> {
    private final OptimisationProblem problem;
    
    @Inject
    public ComponentwiseDecoder(OptimisationProblem problem) {
        this.problem = problem;
    }
    
    @Override
    public SimulationInput decode(ComponentwiseGenotype genotype) {
        DecisionValues dv = new DecisionValues(
                problem.inputConst.getExternalParameters());
        for (String comp : genotype.keySet()) {
            MixedGenotype<String> comp_gt = genotype.get(comp);
            for (Type t : comp_gt.keySet()) {
                MapGenotype<String, ?> type_gt = comp_gt.get(t);
                for (String var : type_gt.getKeys()) {
                    dv.put(comp, var, type_gt.getValue(var));
                }
            }
        }
        SimulationInput inp = new SimulationInput(problem.inputConst);
        try {
            inp.putExpressionValues(dv, problem.inputExprs);
        } catch (ScriptException | InvalidValueException e) {
            throw new RuntimeException(
                    "Input expression evaluation failed", e);
        }
        return inp;
    }
}
