package eu.cityopt.sim.eval;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Results of a successful simulation run with specific input data. Instances
 * have to be constructed 1) by simulator-specific implementation code, and 
 * 2) from database when evaluating metrics for old simulation runs.
 * 
 * @author Hannu Rummukainen
 */
public class SimulationResults extends SimulationOutput implements
        EvaluationContext {
    private BindingLayer bindingLayer;

    public SimulationResults(SimulationInput input, String messages) {
        super(input, messages);
        final Namespace namespace = input.getNamespace();
        this.bindingLayer = new BindingLayer(namespace,
                input.getBindingLayer(),
                (String name) -> {
                    Namespace.Component c = namespace.components.get(name);
                    return (c != null) ? c.outputs : null;
                }, "output variable");
    }

    /** Gets the value object of a named output variable. */
    public Object get(String componentName, String outputName) {
        return bindingLayer.get(componentName, outputName);
    }

    /** Gets an output variable time series. */
    public TimeSeries getTS(String componentName, String outputName) {
        return (TimeSeries) bindingLayer.get(componentName, outputName);
    }

    /**
     * Gets the value of an output variable formatted as a string. Not useful
     * for time series.
     */
    public String getString(String componentName, String outputName) {
        return bindingLayer.getString(componentName, outputName);
    }

    /** Sets the value object of a named output variable. */
    public Object put(String componentName, String outputName, Object value) {
        return bindingLayer.put(componentName, outputName, value);
    }

    /**
     * Parses an input parameter value and stores it. Not useful for time
     * series.
     */
    public Object putString(String componentName, String outputName, String value) {
        return bindingLayer.putString(componentName, outputName, value);
    }

    /** Returns whether all output variables have values. */
    public boolean isComplete() {
        return bindingLayer.isComplete();
    }

    public Namespace getNamespace() {
        return bindingLayer.getNamespace();
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
    public String toString() {
        return bindingLayer.toString();
    }
}
