package eu.cityopt.sim.eval;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Specifies the named parameters, variables and metrics that can be used in
 * expressions. The input parameters and output variables are associated with
 * components. This is a per-project object: only the names and types are
 * defined here. The actual parameter values may vary in different simulation
 * runs.
 * 
 * The same input parameter names and output variable names can occur in
 * multiple components. In expressions, per-component names are referenced as
 * <code>C.N</code> where <code>C</code> is the component name and
 * <code>N</code> is the name of the input or output.
 * 
 * The names of external parameters and metrics are considered top-level names
 * and can be referenced directly, for example the expression <code>M</code>
 * could refer to a metric called <code>M</code>.
 * 
 * The names are bound to values in the following order: components, externals,
 * inputs, outputs, metrics. Names that are bound later can currently shadow
 * earlier names, but we may yet disallow shadowing. The expressions for metrics
 * cannot refer to other metrics, only to the objects that are bound earlier in
 * the above order.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class Namespace {
    /** The expression evaluator to use. */
    public final Evaluator evaluator;

    /** Container for component-specific namespace data. */
    public static class Component {
        /**
         * Simulation model input parameters. Map from parameter name to type.
         */
        public Map<String, Type> inputs;

        /**
         * Simulation model output variables. Map from variable name to type.
         */
        public Map<String, Type> outputs;

        public Component() {
            this.inputs = new HashMap<String, Type>();
            this.outputs = new HashMap<String, Type>();
        }
    }

    /**
     * Model components. Indexed by the component name as a String, for now. In
     * the future the index object may be a more general "component key". For
     * that reason the sim-eval module only uses the hashcode, equals and
     * toString methods on the keys.
     */
    public Map<Object, Component> components;

    /**
     * Constant parameters external to the simulation (ExtParam). Map from
     * parameter name to type.
     */
    public Map<String, Type> externals;

    /**
     * Metrics derived from simulation inputs, outputs and external parameters.
     * Map from metric name to type.
     */
    public Map<String, Type> metrics;

    /** Constructs an empty namespace, given a list of component keys. */
    public Namespace(Evaluator evaluator, Collection<Object> componentKeys) {
        this.evaluator = evaluator;
        this.components = new HashMap<Object, Component>();
        for (Object componentKey : componentKeys) {
            components.put(componentKey, new Component());
        }
        this.externals = new HashMap<String, Type>();
        this.metrics = new HashMap<String, Type>();
    }

    public Namespace(Evaluator evaluator, Map<Object, Component> components,
            Map<String, Type> externals, Map<String, Type> metrics) {
        this.evaluator = evaluator;
        this.components = components;
        this.externals = externals;
        this.metrics = metrics;
    }
}
