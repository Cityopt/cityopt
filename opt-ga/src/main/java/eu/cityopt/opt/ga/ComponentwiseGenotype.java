package eu.cityopt.opt.ga;

import org.opt4j.core.common.random.Rand;
import org.opt4j.core.genotype.CompositeGenotype;

/**
 * A Cityopt genotype with component structure.
 * This is a CompositeGenotype keyed by component names.  The default
 * crossover operator treats each component as a separate "chromosome".
 * Each component is a MixedGenotype keyed by decision variable names.
 * The component key is null for decision variables not associated with a
 * component. 
 * @author ttekth
 *
 */
public class ComponentwiseGenotype
extends CompositeGenotype<String, MixedGenotype<String>> {
    /** Initialise with random values */
    void init(Rand rand) {
        values().forEach(v -> v.init(rand));
    }
}
