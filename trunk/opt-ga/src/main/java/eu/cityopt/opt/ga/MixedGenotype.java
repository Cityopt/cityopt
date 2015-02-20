package eu.cityopt.opt.ga;

import java.util.ArrayList;
import java.util.List;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleMapGenotype;
import org.opt4j.core.genotype.IntegerBounds;
import org.opt4j.core.genotype.IntegerMapGenotype;
import org.opt4j.core.genotype.MapGenotype;

import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.Type;

/**
 * A genotype with named decision variables of mixed type.
 * Currently only real and integer variables are supported.
 * @author ttekth
 *
 * @param <Key> the type of variable names
 */
//XXX Unfortunately MapGenotype is not a Genotype in Opt4J!
public class MixedGenotype<Key> extends CompositeGenotype<Type, Genotype> {
    /**
     * The variables will be grouped by type.  Otherwise the order will be
     * maintained, which may affect crossovers.  Variables of different types
     * are independent.
     * 
     * @param keys a list of variable names
     * @param domains a list of domains in the same order as keys
     */
    public MixedGenotype(List<Key> keys,
                         List<DecisionDomain<?>> domains) {
        super();
        List<Key> realKeys = new ArrayList<>();
        List<Double> realLB = new ArrayList<>();
        List<Double> realUB = new ArrayList<>();
        List<Key> intKeys = new ArrayList<>();
        List<Integer> intLB = new ArrayList<>();
        List<Integer> intUB = new ArrayList<>();
        try {
            for (int i = 0; i != keys.size(); ++i) {
                DecisionDomain<?> dom = domains.get(i);
                switch (dom.getValueType()) {
                case DOUBLE:
                    realKeys.add(keys.get(i));
                    realLB.add((Double)dom.getLowerBound());
                    realUB.add((Double)dom.getUpperBound());
                    break;
                case INTEGER:
                    intKeys.add(keys.get(i));
                    intLB.add((Integer)dom.getLowerBound());
                    intUB.add((Integer)dom.getUpperBound());
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
        put(Type.DOUBLE, new DoubleMapGenotype<Key>(
                realKeys, new DoubleBounds(realLB, realUB)));
        put(Type.INTEGER, new IntegerMapGenotype<Key>(
                intKeys, new IntegerBounds(intLB, intUB)));
    }
    
    public DoubleMapGenotype<Key> getReal() {
        return get(Type.DOUBLE);
    }
    
    public IntegerMapGenotype<Key> getInt() {
        return get(Type.INTEGER);
    }
}
