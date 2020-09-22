package eu.cityopt.opt.ga;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.opt4j.core.genotype.DoubleMapGenotype;
import org.opt4j.core.problem.Decoder;

import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.opt.OptimisationProblem;

public class RealDecoder extends CityoptDecoder
implements Decoder<DoubleMapGenotype<Pair<String, String>>,
                   CityoptPhenotype> {

    @Inject
    public RealDecoder(OptimisationProblem problem) {
        super(problem);
    }

    @Override
    public CityoptPhenotype decode(
            DoubleMapGenotype<Pair<String, String>> genotype) {
        DecisionValues dv = new DecisionValues(
                problem.inputConst.getExternalParameters());
        for (Pair<String, String> key : genotype.getKeys()) {
            dv.put(key.getLeft(), key.getRight(),
                   genotype.getValue(key));
        }
        return makePt(dv);
    }
}
