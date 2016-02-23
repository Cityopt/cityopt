package eu.cityopt.opt.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opt4j.core.Genotype;
import org.opt4j.core.common.random.Rand;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleMapGenotype;
import org.opt4j.core.genotype.IntegerMapGenotype;

import eu.cityopt.sim.eval.Type;

/**
 * A genotype with named decision variables of mixed type.
 * Currently only real and integer variables are supported.
 * Variables are grouped by type and each type is represented by an
 * implementation of MapGenotype<Key, ?>.  Variables of different types are
 * independent in crossovers.
 * @author ttekth
 *
 * @param <Key> the type of variable names
 */
public class MixedGenotype<Key>
extends CompositeGenotype<Type, Genotype> {
    public DoubleMapGenotype<Key> getReal() {
        return get(Type.DOUBLE);
    }
    
    public IntegerMapGenotype<Key> getInt() {
        return get(Type.INTEGER);
    }

    /**
     * Initialise with random values. 
     * 
     * @param rand random number generator
     */
    public void init(Rand rand) {
        List<Type> sorted = new ArrayList<>(keySet());
        Collections.sort(sorted);
        for (Type t : sorted) {
            switch (t) {
            case DOUBLE:
                getReal().init(rand);
                break;
            case INTEGER:
                getInt().init(rand);
                break;
            default:
                throw new IllegalArgumentException(
                        "Don't know how to init type " + t);
            }
        }
    }
}
