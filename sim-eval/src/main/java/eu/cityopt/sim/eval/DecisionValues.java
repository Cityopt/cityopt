package eu.cityopt.sim.eval;

import java.text.ParseException;
import java.util.Collection;

import javax.script.Bindings;

/**
 * Container for decision variable values. This is only needed in scenario
 * generation optimization.
 * <p>
 * Decision variables can be defined as top-level names as well as inside named
 * components; the latter is intended to make it easy to use the same name for a
 * simulation input variable and a decision variable.
 *
 * @author Hannu Rummukainen
 */
public class DecisionValues implements EvaluationContext {
    private final BindingLayer bindingLayer;
    private final ExternalParameters externalParameters;

    /**
     * Constructs an empty instance.
     * @param externalParameters instance to be used in the evaluation
     *  of input expressions.
     */
    public DecisionValues(ExternalParameters externalParameters) {
        final Namespace namespace = externalParameters.getNamespace();
        this.bindingLayer = new BindingLayer(
                namespace, externalParameters.getBindingLayer(),
                (String name) -> {
                    if (name != null) {
                        Namespace.Component c = namespace.components.get(name);
                        return (c != null) ? c.decisions : null;
                    } else {
                        return namespace.decisions;
                    }
                },
                "decision variable");
        this.externalParameters = externalParameters;
    }

    /** Copy constructor. */
    public DecisionValues(DecisionValues other) {
        this.bindingLayer = new BindingLayer(other.bindingLayer);
        this.externalParameters = other.externalParameters;
    }

    ExternalParameters getExternalParameters() {
        return externalParameters;
    }

    public Namespace getNamespace() {
        return bindingLayer.getNamespace();
    }

    /**
     * Gets the value of a named decision variable.
     * Use null componentName for a top-level decision variable.
     */
    public Object get(String componentName, String decisionName) {
        return bindingLayer.get(componentName, decisionName);
    }

    /** Gets the value of a decision variable. */
    public Object get(DecisionVariable variable) {
        return get(variable.componentName, variable.name);
    }

    /**
     * Sets the value of a named decision variable.
     * Use null componentName for a top-level decision variable.
     */
    public Object put(String componentName, String decisionName, Object value) {
        return bindingLayer.put(componentName, decisionName, value);
    }

    /** Sets the value of a decision variable. */
    public void put(DecisionVariable variable, Object value) {
        put(variable.componentName, variable.name, value);
    }

    /**
     * Gets the value of a named decision variable as a formatted string.
     * Use null componentName for a top-level decision variable.
     */
    public String getString(String componentName, String decisionName) {
        return bindingLayer.getString(componentName, decisionName);
    }

    /**
     * Parses the value of a named decision variable and stores it.
     * Use null componentName for a top-level decision variable.
     * @throws ParseException if the string cannot be parsed as the correct type 
     */
    public Object putString(String componentName, String decisionName, String value)
            throws ParseException {
        return bindingLayer.putString(componentName, decisionName, value);
    }

    /** Whether the named decision variable has a value. */
    public boolean contains(String componentName, String decisionName) {
        return bindingLayer.contains(componentName, decisionName);
    }

    /** Returns whether all decision variables have a value. */
    public boolean isComplete() {
        return bindingLayer.isComplete();
    }

    @Override
    public BindingLayer getBindingLayer() {
        return bindingLayer;
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
    public boolean equals(Object other) {
        if (!(other instanceof DecisionValues)) {
            return false;
        } else if (other == this) {
            return true;
        } else {
            return bindingLayer.localBindingsEqual(
                    ((DecisionValues) other).bindingLayer);
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

    /** Formats the values to a string in the given order. */
    public String formatString(Collection<DecisionVariable> variableOrder) {
        return bindingLayer.formatString(variableOrder);
    }
}
