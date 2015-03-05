package eu.cityopt.opt.ga;

import java.util.ArrayList;
import java.util.List;

import org.opt4j.core.common.random.Rand;
import org.opt4j.core.genotype.Bounds;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleMapGenotype;
import org.opt4j.core.genotype.IntegerBounds;
import org.opt4j.core.genotype.IntegerMapGenotype;
import org.opt4j.core.problem.Creator;

import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.Type;

public class MixedCreator<Key> implements Creator<MixedGenotype<Key>> {
    private final Rand rand;
    private final List<Key> realKeys, intKeys;
    private final Bounds<Double> realBounds;
    private final Bounds<Integer> intBounds;
    
    /**
     * Constructor.  The decision variables are separated by type.
     * Variables of each type are kept in the same order as they appear
     * in the arguments.
     * @param rand
     * @param keys list of decision variable names
     * @param domains variable domains in the same order as keys
     */
    public MixedCreator(
            Rand rand,
            List<Key> keys,
            List<DecisionDomain> domains) {
        this.rand = rand;
        realKeys = new ArrayList<>();
        intKeys = new ArrayList<>();
        List<Double> realLB = new ArrayList<>();
        List<Double> realUB = new ArrayList<>();
        List<Integer> intLB = new ArrayList<>();
        List<Integer> intUB = new ArrayList<>();
        try {
            for (int i = 0; i != keys.size(); ++i) {
                DecisionDomain dom = domains.get(i);
                switch (dom.getValueType()) {
                case DOUBLE:
                    @SuppressWarnings("unchecked")
                    NumericInterval<Double> dd
                        = (NumericInterval<Double>)dom;
                    realKeys.add(keys.get(i));
                    realLB.add(dd.getLowerBound());
                    realUB.add(dd.getUpperBound());
                    break;
                case INTEGER:
                    @SuppressWarnings("unchecked")
                    NumericInterval<Integer> di
                        = (NumericInterval<Integer>)dom;
                    intKeys.add(keys.get(i));
                    intLB.add((Integer)di.getLowerBound());
                    intUB.add((Integer)di.getUpperBound());
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Invalid decision variable type "
                            + dom.getValueType());    
                }
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                    "Decision domain mismatch", e);
        }
        realBounds = new DoubleBounds(realLB, realUB);
        intBounds = new IntegerBounds(intLB, intUB);
    }

    @Override
    public MixedGenotype<Key> create() {
        MixedGenotype<Key> g = new MixedGenotype<>();
        g.put(Type.DOUBLE, new DoubleMapGenotype<Key>(
                realKeys, realBounds));
        g.put(Type.INTEGER, new IntegerMapGenotype<Key>(
                intKeys, intBounds));
        g.init(rand);
        return g;
    }
}
