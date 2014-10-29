package eu.cityopt.sim.eval;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * Results of a successful simulation run with specific input data.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class SimulationResults extends SimulationOutput implements EvaluationContext {
    private BindingLayer bindingLayer;

    public SimulationResults(SimulationInput input, String messages,
            Bindings values) {
        super(input, messages);
        bindingLayer = new BindingLayer(values, input.getBindingLayer(),
                input.getNamespace().outputs, "output variable");
    }

    public SimulationResults(SimulationInput input, String messages) {
        this(input, messages, new SimpleBindings());
    }

    /** Gets the value object of an output variable. */
    public Object get(String outputName) {
        return bindingLayer.get(outputName);
    }

    /** Gets an output variable time series. */
    public TimeSeries getTS(String outputName) {
        return (TimeSeries) bindingLayer.get(outputName);
    }

    /** Sets the value object of an output variable. */
    public Object put(String outputName, Object value) {
        return bindingLayer.put(outputName, value);
    }

    /** Returns whether all output variables have values. */
    public boolean isComplete() {
        return bindingLayer.isComplete();
    }

    public Namespace getNamespace() {
        return getInput().getNamespace();
    }

    @Override
    public BindingLayer getBindingLayer() {
        return bindingLayer;
    }

    @Override
    public Bindings toBindings() {
        return bindingLayer.toBindings();
    }
}
