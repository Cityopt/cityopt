package eu.cityopt.sim.eval;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Context for evaluating constraints with current decision variable values.
 *
 * @author Hannu Rummukainen
 */
public class ConstraintContext implements EvaluationContext {
    private final BindingLayer bindingLayer;
    private final SimulationInput input;
    private final MetricValues metricValues;

    /**
     * Constructs a context for evaluating pre-simulation constraints
     * with external parameters, decision variables and input paramters
     * defined.
     * @param decisionValues decision variable values
     * @param input complete simulation input
     */
    public ConstraintContext(
            DecisionValues decisionValues, SimulationInput input) {
        if (input.getExternalParameters() != decisionValues.getExternalParameters()
                || !input.isComplete()) {
            throw new IllegalArgumentException();
        }
        this.bindingLayer = new BindingLayer(
                input.getBindingLayer(), decisionValues.getBindingLayer());
        this.input = input;
        this.metricValues = null;
    }

    /**
     * Constructs a context for evaluating post-simulation constraints
     * with external parameters, decision variables, input parameters,
     * output variables and metrics defined.
     * @param preconstraintContext pre-simulation constraint context
     *   containing the simulation input parameter and decision variable
     *   values.
     * @param metricValues contains the metric values and a reference to
     *   the simulation results
     */
    public ConstraintContext(
            ConstraintContext preconstraintContext,
            MetricValues metricValues) {
        SimulationResults results = metricValues.getResults();
        if (preconstraintContext.metricValues != null
                || results.getInput() != preconstraintContext.input) {
            throw new IllegalArgumentException();
        }
        BindingLayer newResultLayer = new BindingLayer(
                results.getBindingLayer(), preconstraintContext.bindingLayer);
        this.bindingLayer = new BindingLayer(
                metricValues.getBindingLayer(), newResultLayer);
        this.input = preconstraintContext.input;
        this.metricValues = metricValues;
    }

    @Override
    public Bindings toBindings() throws ScriptException {
        return bindingLayer.toBindings();
    }

    @Override
    public EvaluationSetup getEvaluationSetup() {
        return input.getNamespace();
    }

    @Override
    public BindingLayer getBindingLayer() {
        return bindingLayer;
    }
}
