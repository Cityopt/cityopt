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

    /** Metric values, in the same order as the constructor arguments. */
    public final double[] metricValues;

    public MetricValues(SimulationResults results,
            Collection<MetricExpression> metrics) throws ScriptException,
            InvalidValueException {
        this.results = results;
        this.metricValues = new double[metrics.size()];
        final Namespace namespace = results.getNamespace();
        this.bindingLayer = new BindingLayer(namespace,
                results.getBindingLayer(),
                name -> (name == null) ? namespace.metrics : null,
                "metric");
        int i = 0;
        for (MetricExpression metric : metrics) {
            double value = metric.evaluate(results);
            bindingLayer.put(null, metric.getMetricName(), value);
            metricValues[i] = value;
            ++i;
        }
    }

    public SimulationResults getResults() {
        return results;
    }

    /** Gets the value of a named metric. */
    public double get(String metricName) {
        return (Double) bindingLayer.get(null, metricName);
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
