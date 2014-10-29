package eu.cityopt.sim.eval;

import javax.script.ScriptException;

public class MetricExpression extends DoubleExpression {
	private final int metricId;
	private final String metricName;

	public MetricExpression(int metricId, String metricName,
			String source, Evaluator evaluator) throws ScriptException {
		super(source, evaluator);
		this.metricId = metricId;
		this.metricName = metricName;
	}

	public int getMetricId() {
		return metricId;
	}

	public String getMetricName() {
		return metricName;
	}
}
