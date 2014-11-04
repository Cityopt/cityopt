package eu.cityopt.sim.eval;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Container for external parameter values.
 *
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class ExternalParameters implements EvaluationContext {
    private BindingLayer bindingLayer;

    public ExternalParameters(final Namespace namespace) {
        this.bindingLayer = new BindingLayer(
                namespace, null,
                new BindingLayer.ComponentNamespaces() {
                    public Map<String, Type> get(Object key) {
                        return (key == null) ? namespace.externals : null;
                    }
                }, "external parameter");
    }

    public Namespace getNamespace() {
        return bindingLayer.getNamespace();
    }

    /** Gets the value of a named external parameter. */
    public Object get(String externalName) {
        return bindingLayer.get(null, externalName);
    }

    /** Sets the value of a named external parameter. */
    public Object put(String externalName, Object value) {
        return bindingLayer.put(null, externalName, value);
    }

    /**
     * Gets the value of a named external parameter as a formatted string.
     * Not useful for time series.
     */
    public String getString(String externalName) {
        return bindingLayer.getString(null, externalName);
    }

    /**
     * Parses the value of a named external parameter and stores it.
     * Not useful for time series.
     */
    public Object putString(String externalName, String value) {
        return bindingLayer.putString(null, externalName, value);
    }

    @Override
    public Bindings toBindings() throws ScriptException {
        return bindingLayer.toBindings();
    }

    @Override
    public BindingLayer getBindingLayer() {
        return bindingLayer;
    }

    @Override
    public String toString() {
        return bindingLayer.toString();
    }
}
