package eu.cityopt.opt.ga;

import org.opt4j.core.common.random.Rand;
import org.opt4j.core.genotype.CompositeGenotype;

public class ComponentwiseGenotype
extends CompositeGenotype<String, MixedGenotype<String>> {
    /** Initialise with random values */
    void init(Rand rand) {
        values().forEach(v -> v.init(rand));
    }
}
