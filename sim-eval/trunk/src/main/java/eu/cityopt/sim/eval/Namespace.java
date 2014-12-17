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
 * <p>
 * The same input parameter names and output variable names can occur in
 * multiple components. In expressions, per-component names are referenced as
 * <code>C.N</code> where <code>C</code> is the component name and
 * <code>N</code> is the name of the input or output.
 * <p>
 * The names of external parameters and metrics are considered top-level names
 * and can be referenced directly, for example the expression <code>M</code>
 * could refer to a metric called <code>M</code>.
 * <p>
 * The names are bound to values in the following order: components, externals,
 * inputs, outputs, metrics. Names that are bound later can currently shadow
 * earlier names, but we may yet disallow shadowing. The expressions for metrics
 * cannot refer to other metrics, only to the objects that are bound earlier in
 * the above order.
 * <p>
 * In order to support generation of new simulation inputs in an optimization
 * procedure, there is also an option to define named decision variables. When
 * evaluating simulation input expressions, only external parameters and named
 * decision variables are bound. Named decision variables cannot be referenced
 * in other contexts such as constraint or metric expressions.
 *
 * @author Hannu Rummukainen
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

        /**
         * Component-level decision variables for use in scenario generation optimization.
         * This is optional, and can be left null in other use cases.
         */
        public Map<String, Type> decisions;

        public Component() {
            this(false);
        }

        public Component(boolean useDecisions) {
            this.inputs = new HashMap<>();
            this.outputs = new HashMap<>();
            this.decisions = useDecisions ? new HashMap<>() : null;
        }
    }

    /**
     * Model components. Indexed by the component name as a String.
     */
    public Map<String, Component> components;

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

    /**
     * Top-level decision variables for use in scenario generation optimization.
     * This is optional, and can be left empty in other use cases.
     */
    public Map<String, Type> decisions;

    /**
     * Constructs an empty namespace, given a list of component names. The
     * component names should be unique.
     */
    public Namespace(Evaluator evaluator, Collection<String> componentNames) {
        this(evaluator, componentNames, false);
    }

    /**
     * Constructs an empty namespace, given a list of component names.
     * @param evaluator the expression evaluator with which the constructed
     *   Namespace is to be used 
     * @param componentNames collection of unique component names
     * @param useDecisions whether to initialize the decisions fields.
     *   Should be set true for scenario generation optimization.
     */
    public Namespace(Evaluator evaluator, Collection<String> componentNames,
            boolean useDecisions) {
        this.evaluator = evaluator;
        this.components = new HashMap<>();
        for (String componentName : componentNames) {
            components.put(componentName, new Component(useDecisions));
        }
        this.externals = new HashMap<>();
        this.metrics = new HashMap<>();
        this.decisions = useDecisions ? new HashMap<>() : null;
    }

    public Namespace(Evaluator evaluator, Map<String, Component> components,
            Map<String, Type> externals, Map<String, Type> metrics,
            Map<String, Type> decisions) {
        this.evaluator = evaluator;
        this.components = components;
        this.externals = externals;
        this.metrics = metrics;
        this.decisions = decisions;
    }
}
