package eu.cityopt.sim.eval;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Results of a successful simulation run with specific input data. Instances
 * have to be constructed 1) by simulator-specific implementation code, and 
 * 2) from database when evaluating metrics for old simulation runs.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class SimulationResults extends SimulationOutput implements
        EvaluationContext {
    private BindingLayer bindingLayer;

    public SimulationResults(SimulationInput input, String messages) {
        super(input, messages);
        final Namespace namespace = input.getNamespace();
        this.bindingLayer = new BindingLayer(namespace,
                input.getBindingLayer(),
                new BindingLayer.ComponentNamespaces() {
                    public Map<String, Type> get(Object key) {
                        Namespace.Component c = namespace.components.get(key);
                        return (c != null) ? c.outputs : null;
                    }
                }, "output variable");
    }

    /** Gets the value object of a named output variable. */
    public Object get(Object componentKey, String outputName) {
        return bindingLayer.get(componentKey, outputName);
    }

    /** Gets an output variable time series. */
    public TimeSeries getTS(Object componentKey, String outputName) {
        return (TimeSeries) bindingLayer.get(componentKey, outputName);
    }

    /**
     * Gets the value of an output variable formatted as a string. Not useful
     * for time series.
     */
    public String getString(Object componentKey, String outputName) {
        return bindingLayer.getString(componentKey, outputName);
    }

    /** Sets the value object of a named output variable. */
    public Object put(Object componentKey, String outputName, Object value) {
        return bindingLayer.put(componentKey, outputName, value);
    }

    /**
     * Parses an input parameter value and stores it. Not useful for time
     * series.
     */
    public Object putString(Object componentKey, String outputName, String value) {
        return bindingLayer.putString(componentKey, outputName, value);
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
