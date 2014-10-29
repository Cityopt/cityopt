package eu.cityopt.sim.eval;

import java.util.Collection;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public class MetricValues implements EvaluationContext {
	private SimulationResults results;
	private BindingLayer bindingLayer;

	/** Metric values, in the same order as the constructor arguments. */
	public final double[] metricValues;

	public MetricValues(SimulationResults results, Collection<MetricExpression> metrics)
			throws ScriptException, InvalidValueException {
		this.results = results;
		this.metricValues = new double[metrics.size()];
		this.bindingLayer = new BindingLayer(
				new SimpleBindings(), results.getBindingLayer(),
				results.getNamespace().metrics, "metric");
		int i = 0;
		for (MetricExpression metric : metrics) {
			double value = metric.evaluate(results);
			bindingLayer.put(metric.getMetricName(), value);
			metricValues[i] = value;
			++i;
		}
	}

	SimulationResults getResults() {
		return results;
	}

	double get(String metricName) {
		return (Double)bindingLayer.get(metricName);
	}

	@Override
	public Bindings toBindings() {
		return bindingLayer.toBindings();
	}

	@Override
	public BindingLayer getBindingLayer() {
		return bindingLayer;
	}
}
