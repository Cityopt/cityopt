package eu.cityopt.sim.eval;

import java.util.Collection;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Container for values of named metrics from a specific simulation run. Metric
 * values are intended to be recomputed from SimulationResults when needed.
 * An instance of this class is needed to compute objective values.
 *
 * @author Hannu Rummukainen
 */
public class MetricValues implements EvaluationContext {
    private SimulationResults results;
    private BindingLayer bindingLayer;

    public MetricValues(SimulationResults results,
            Collection<MetricExpression> metrics) throws ScriptException,
            InvalidValueException {
        this.results = results;
        final Namespace namespace = results.getNamespace();
        this.bindingLayer = new BindingLayer(namespace,
                results.getBindingLayer(),
                name -> (name == null) ? namespace.metrics : null,
                "metric");
        for (MetricExpression metric : metrics) {
            Object value = metric.evaluate(results);
            bindingLayer.put(null, metric.getMetricName(), value);
        }
    }

    public SimulationResults getResults() {
        return results;
    }

    /** Gets the value of a named metric. */
    public Object get(String metricName) {
        return bindingLayer.get(null, metricName);
    }

    /** Gets the value of a named metric as a formatted string. */
    public String getString(String metricName) {
        return bindingLayer.getString(null, metricName);
    }

    @Override
    public Bindings toBindings() throws ScriptException {
        return bindingLayer.toBindings();
    }

    @Override
    public EvaluationSetup getEvaluationSetup() {
        return bindingLayer.getNamespace();
    }

    @Override
    public BindingLayer getBindingLayer() {
        return bindingLayer;
    }

    @Override
    public String toString() {
        return bindingLayer.toString();
    }
}
