package eu.cityopt.opt.io;

import javax.script.ScriptException;

import eu.cityopt.opt.io.JacksonBinder.Metric;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.opt.SimulationStructure;

public class SimulationStructureBuilder extends
        AbstractBuilder<SimulationStructure> {
    protected final Namespace ns = result.getNamespace();

    public SimulationStructureBuilder(SimulationStructure initial) {
        super(initial);
    }

    @Override
    protected void add(Metric m) throws ScriptException {
        if (m.expression == null)
            throw new IllegalArgumentException("Missing expression");
        result.metrics.add(new MetricExpression(
                null, m.name, m.expression, ns.evaluator));
    }
}