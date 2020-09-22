package eu.cityopt.opt.ga;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.opt4j.core.common.random.Rand;
import org.opt4j.core.genotype.Bounds;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleMapGenotype;
import org.opt4j.core.problem.Creator;

import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationProblem;

public class RealCreator
implements Creator<DoubleMapGenotype<Pair<String, String>>> {
    public final Rand rand;
    public final List<Pair<String, String>> keys;
    public final Bounds<Double> bounds;

    @Inject
    public RealCreator(Rand rand, OptimisationProblem problem) {
        this.rand = rand;
        int len = problem.decisionVars.size();
        keys = new ArrayList<>(len);
        List<Double>
          lb = new ArrayList<>(len),
          ub = new ArrayList<>(len);
        // TODO Auto-generated constructor stub
        for (DecisionVariable dv : problem.decisionVars) {
            Type typ = dv.domain.getValueType();
            if (typ != Type.DOUBLE)
                throw new IllegalArgumentException(
                        "Invalid decision variable type " + typ
                        + " (only double is allowed)");
            @SuppressWarnings("unchecked")
            NumericInterval<Double>
                dd = (NumericInterval<Double>)dv.domain;
            keys.add(Pair.of(dv.componentName, dv.name));
            lb.add(dd.getLowerBound());
            ub.add(dd.getUpperBound());
        }
        bounds = new DoubleBounds(lb, ub);
    }

    @Override
    public DoubleMapGenotype<Pair<String, String>> create() {
        DoubleMapGenotype<Pair<String, String>>
            g = new DoubleMapGenotype<>(keys, bounds);
        g.init(rand);
        return g;
    }
}
