package eu.cityopt.sim.eval;

import java.text.ParseException;
import java.util.Collection;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Container for simulation input data. Instances have to be constructed not
 * only when starting new simulations, but also when evaluating constraint
 * feasibility or metric values for previously run simulations.
 *
 * The hashCode and equals methods consider the equality of all input parameter
 * values, so that the class can be used as a map key.
 *
 * Implements the EvalutionContext interface, providing access to both external
 * parameters and the defined simulation inputs.  This makes it possible for
 * pre-simulation constraints to refer to external parameter values.
 *
 * @author Hannu Rummukainen
 */
public class SimulationInput implements EvaluationContext {
    private ExternalParameters externalParameters;
    private BindingLayer bindingLayer;

    /**
     * Constructs an empty SimulationInput.
     * @param externalParameters instance to be used in the evaluation
     *  of constraints, metrics and objectives
     */
    public SimulationInput(ExternalParameters externalParameters) {
        this.externalParameters = externalParameters;
        final Namespace namespace = externalParameters.getNamespace(); 
        this.bindingLayer = new BindingLayer(
                namespace, externalParameters.getBindingLayer(),
                (String name) -> {
                    Namespace.Component c = namespace.components.get(name);
                    return (c != null) ? c.inputs : null;
                }, "input parameter");
    }

    /**
     * Constructs a SimulationInput based on optimization decision variables.
     * 
     * @param decisions
     *            the values of the decision variables referenced in expressions
     * @param inputExpressions
     *            collection of input expressions associated with specific input
     *            variables
     * @see #putExpressionValues(DecisionValues, Collection)
     */
    public SimulationInput(DecisionValues decisions,
            Collection<InputExpression> inputExpressions)
                    throws ScriptException, InvalidValueException {
        this(decisions.getExternalParameters());
        putExpressionValues(decisions, inputExpressions);
    }

    /**
     * Copy constructor. Use this if you need to create a modified input after
     * the previous SimulationInput has been passed to SimulationRunner.start
     */
    public SimulationInput(SimulationInput other) {
        this.bindingLayer = new BindingLayer(other.bindingLayer);
    }

    public Namespace getNamespace() {
        return bindingLayer.getNamespace();
    }

    public ExternalParameters getExternalParameters() {
        return externalParameters;
    }

    /** Gets the value of a named input parameter. */
    public Object get(String componentName, String inputName) {
        return bindingLayer.get(componentName, inputName);
    }

    /** Sets the value of a named input parameter. */
    public Object put(String componentName, String inputName, Object value) {
        return bindingLayer.put(componentName, inputName, value);
    }

    /** Gets the value of an input parameter formatted as a string. */
    public String getString(String componentName, String inputName) {
        return bindingLayer.getString(componentName, inputName);
    }

    /**
     * Parses an input parameter value and stores it.
     * @throws ParseException if the string cannot be parsed as the correct type
     */
    public Object putString(String componentName, String inputName, String value)
            throws ParseException {
        return bindingLayer.putString(componentName, inputName, value);
    }

    /**
     * Sets the values of input variables by evaluating input expressions.
     * 
     * @param decisions
     *            the values of the decision variables referenced in expressions
     * @param inputExpressions
     *            collection of input expressions associated with specific input
     *            variables
     */
    public void putExpressionValues(
            DecisionValues decisions, Collection<InputExpression> inputExpressions)
                    throws ScriptException, InvalidValueException {
        for (InputExpression expression : inputExpressions) {
            Object value = expression.evaluate(decisions);
            put(expression.getComponentName(), expression.getInputName(), value);
        }
    }

    /** Returns whether all input parameters have a value. */
    public boolean isComplete() {
        return bindingLayer.isComplete();
    }

    @Override
    public BindingLayer getBindingLayer() {
        return bindingLayer;
    }

    @Override
    public EvaluationSetup getEvaluationSetup() {
        return bindingLayer.getNamespace();
    }

    @Override
    public Bindings toBindings() throws ScriptException {
        return bindingLayer.toBindings();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SimulationInput)) {
            return false;
        } else if (other == this) {
            return true;
        } else {
            return bindingLayer.localBindingsEqual(
                    ((SimulationInput) other).bindingLayer);
        }
    }

    @Override
    public int hashCode() {
        return bindingLayer.localHashCode();
    }

    @Override
    public String toString() {
        return bindingLayer.toString();
    }
}
