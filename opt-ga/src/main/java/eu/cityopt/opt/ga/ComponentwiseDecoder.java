package eu.cityopt.opt.ga;

import org.opt4j.core.genotype.MapGenotype;
import org.opt4j.core.problem.Decoder;

import javax.inject.Inject;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationProblem;

public class ComponentwiseDecoder extends CityoptDecoder
implements Decoder<ComponentwiseGenotype, CityoptPhenotype> {

    @Inject
    public ComponentwiseDecoder(OptimisationProblem problem) {
        super(problem);
    }

    @Override
    public CityoptPhenotype decode(ComponentwiseGenotype genotype) {
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
        return makePt(dv);
    }
}
