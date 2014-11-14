package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * Represents a named metric. Technically, a metric is a named variable that is
 * computed after simulation results are available, and which can be used in
 * objective function expressions. The purpose of the feature is to avoid
 * repeating the expressions for e.g. CO2 emissions or operational costs in
 * multiple places.
 *
 * @author Hannu Rummukainen
 */
public class MetricExpression extends DoubleExpression {
    private final int metricId;
    private final String metricName;

    public MetricExpression(int metricId, String metricName, String source,
            Evaluator evaluator) throws ScriptException {
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
