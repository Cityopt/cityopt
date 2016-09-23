package eu.cityopt.sim.eval;

import java.text.ParseException;
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

    /**
     * Creates an empty instance, which has to be filled in manually
     * by calling {@link #evaluate(MetricExpression)} or {@link #put(String, Object)}.
     */
    public MetricValues(SimulationResults results) {
        this.results = results;
        final Namespace namespace = results.getNamespace();
        this.bindingLayer = new BindingLayer(namespace,
                results.getBindingLayer(),
                name -> (name == null) ? namespace.metrics : null,
                "metric");
    }

    /**
     * Evaluates metric values using the given collection of metric expressions.
     */
    public MetricValues(SimulationResults results,
            Collection<MetricExpression> metrics) throws ScriptException {
        this(results);
        if (metrics != null) {
            for (MetricExpression metric : metrics) {
                evaluate(metric);
            }
        }
    }

    /**
     * Evaluates and stores the value of a single metric expression.
     */
    public void evaluate(MetricExpression metric) throws ScriptException {
        Type type = getNamespace().metrics.get(metric.getMetricName());
        Object value = metric.evaluateAs(type, results);
        bindingLayer.put(null, metric.getMetricName(), value);
    }

    public SimulationResults getResults() {
        return results;
    }

    public Namespace getNamespace() {
        return bindingLayer.getNamespace();
    }

    /** Gets the value of a named metric. */
    public Object get(String metricName) {
        return bindingLayer.get(null, metricName);
    }

    /** Gets the value of a named metric that is a time series. */
    public TimeSeriesI getTS(String metricName) {
        return (TimeSeriesI) bindingLayer.get(null, metricName);
    }

    /**
     * Gets the value of a named metric as a formatted string.
     * Not useful for time series.
     */
    public String getString(String metricName) {
        return bindingLayer.getString(null, metricName);
    }

    /** Sets the value of a named metric. */
    public void put(String metricName, Object value) {
        bindingLayer.put(null, metricName, value);
    }

    /**
     * Parses a metric value and stores it. Not useful for time series.
     * @throws ParseException if the string cannot be parsed as the correct type
     */
    public Object putString(String metricName, String value) throws ParseException {
        return bindingLayer.putString(null, metricName, value);
    }

    @Override
    public Bindings toBindings() {
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
