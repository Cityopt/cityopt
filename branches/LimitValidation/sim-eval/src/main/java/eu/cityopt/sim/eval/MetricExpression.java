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
public class MetricExpression extends Expression {
    private final Integer metricId;

    public MetricExpression(Integer metricId, String metricName, String source,
            Evaluator evaluator) throws ScriptException {
        super(source, metricName, evaluator);
        this.metricId = metricId;
    }

    public Integer getMetricId() {
        return metricId;
    }

    public String getMetricName() {
        return name;
    }

    @Override
    protected String kind() {
        return "metric";
    }
}
