package eu.cityopt.sim.eval;

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
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class SimulationInput implements EvaluationContext {
    private BindingLayer bindingLayer;

    public SimulationInput(ExternalParameters externalParameters) {
        final Namespace namespace = externalParameters.getNamespace(); 
        this.bindingLayer = new BindingLayer(
                namespace, externalParameters.getBindingLayer(),
                (String name) -> {
                    Namespace.Component c = namespace.components.get(name);
                    return (c != null) ? c.inputs : null;
                }, "input parameter");
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

    /** Parses an input parameter value and stores it. */
    public Object putString(String componentName, String inputName, String value) {
        return bindingLayer.putString(componentName, inputName, value);
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
            return bindingLayer.getLocalBindings().equals(
                    ((SimulationInput) other).bindingLayer.getLocalBindings());
        }
    }

    @Override
    public int hashCode() {
        return bindingLayer.getLocalBindings().hashCode();
    }

    @Override
    public String toString() {
        return bindingLayer.toString();
    }
}
