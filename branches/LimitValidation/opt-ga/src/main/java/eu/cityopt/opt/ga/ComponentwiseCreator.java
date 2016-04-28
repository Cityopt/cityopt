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

import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.opt.OptimisationProblem;

public class ComponentwiseCreator
implements Creator<ComponentwiseGenotype> {
    private final Map<String, MixedCreator<String>> creators;
    
    @Inject
    ComponentwiseCreator(Rand rand, OptimisationProblem problem) {
        creators = new HashMap<>();
        Map<String, List<DecisionVariable>> cdv = new HashMap<>();
        for (DecisionVariable dv : problem.decisionVars) {
            cdv.computeIfAbsent(dv.componentName, k -> new ArrayList<>())
                .add(dv);
        }
        for (Map.Entry<String, List<DecisionVariable>> kv : cdv.entrySet()) {
            List<DecisionVariable> dvs = kv.getValue();
            Collections.sort(dvs, (d, e) -> d.name.compareTo(e.name));
            creators.put(kv.getKey(), new MixedCreator<String>(rand,
                    Lists.transform(dvs, dv -> dv.name),
                    Lists.transform(dvs, dv -> dv.domain)));
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
