package eu.cityopt.opt.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.opt4j.core.common.random.Rand;
import org.opt4j.core.problem.Creator;

import com.google.common.collect.Lists;

import eu.cityopt.sim.eval.DecisionDomain;

public class ComponentwiseCreator
implements Creator<ComponentwiseGenotype> {
    private final Map<String, MixedCreator<String>> creators;
    
    @Inject
    ComponentwiseCreator(Rand rand, OptimisationProblem problem) {
        creators = new HashMap<>();
        for (Map.Entry<String, Map<String, DecisionDomain>>
                 kv : problem.decisionVars.entrySet()) {
            Map<String, DecisionDomain> dvs = kv.getValue();
            List<String> names = new ArrayList<>(dvs.keySet());
            Collections.sort(names);
            creators.put(kv.getKey(), new MixedCreator<String>(rand, names,
                    Lists.transform(names, name -> dvs.get(names))));
        }
    }

    @Override
    public ComponentwiseGenotype create() {
        ComponentwiseGenotype g = new ComponentwiseGenotype();
        for (Map.Entry<String, MixedCreator<String>>
                 kv : creators.entrySet()) {
            g.put(kv.getKey(), kv.getValue().create());
        }
        return g;
    }
}
