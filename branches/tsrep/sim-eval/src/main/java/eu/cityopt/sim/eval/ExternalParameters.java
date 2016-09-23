package eu.cityopt.sim.eval;

import java.text.ParseException;

import javax.script.Bindings;

/**
 * Container for external parameter values.
 *
 * @author Hannu Rummukainen
 */
public class ExternalParameters implements EvaluationContext {
    private BindingLayer bindingLayer;
    private volatile Integer externalId;

    public ExternalParameters(final Namespace namespace) {
        this.bindingLayer = new BindingLayer(
                namespace, null,
                name -> (name == null) ? namespace.externals : null,
                "external parameter");
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

    /** Gets the value of a named external parameter that is a time series. */
    public TimeSeriesI getTS(String externalName) {
        return (TimeSeriesI) bindingLayer.get(null, externalName);
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
     * @throws ParseException if the string cannot be parsed as the correct type
     */
    public Object putString(String externalName, String value) throws ParseException {
        return bindingLayer.putString(null, externalName, value);
    }

    /** Whether the named external parameter has been set. */
    public boolean contains(String externalName) {
        return bindingLayer.contains(null, externalName);
    }

    /** Return whether all external parameters have been set. */
    public boolean isComplete() {
        return bindingLayer.isComplete();
    }

    /** Externally provided identifier, or null. */
    public Integer getExternalId() {
        return externalId;
    }

    public void setExternalId(Integer value) {
        externalId = value;
    }

    @Override
    public Bindings toBindings() {
        return bindingLayer.toBindings();
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
    public String toString() {
        return bindingLayer.toString();
    }
}
