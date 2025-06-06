package eu.cityopt.sim.eval;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
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
 * <p>
 * Do not modify the Namespace after creating {@link BindingLayer}s from it.
 * Creating an {@link ExternalParameters} creates a {@link BindingLayer}.
 *
 * @author Hannu Rummukainen
 */
public class Namespace extends EvaluationSetup {
    /**
     * Name of virtual component for simulator configuration parameters.
     * The component only exists inside the CityOPT software, and it is
     * not connected to the actual simulation model.
     */
    public static final String CONFIG_COMPONENT = "CITYOPT";

    /**
     * Name of input parameter in the virtual configuration component:
     * Simulation start time in seconds. Relative to the simulation time origin.
     * The input parameter must be of type TIMESTAMP.
     */
    public static final String CONFIG_SIMULATION_START = "simulation_start";

    /**
     * Name of input parameter in the virtual configuration component:
     * Simulation end time in seconds. Relative to the simulation time origin.
     * The input parameter must be of type TIMESTAMP.
     */
    public static final String CONFIG_SIMULATION_END = "simulation_end";

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

    /** Constructs an empty namespace. */
    public Namespace(Evaluator ev, Instant timeOrigin) {
        this(ev, timeOrigin, Collections.emptySet(), false);
    }

    /** Constructs an empty namespace. */
    public Namespace(Evaluator ev, Instant timeOrigin, boolean useDecisions) {
        this(ev, timeOrigin, Collections.emptySet(), useDecisions);
    }

    /**
     * Constructs a namespace containing just the named empty components.
     * The component names should be unique.
     */
    public Namespace(Evaluator evaluator, Instant timeOrigin,
            Collection<String> componentNames) {
        this(evaluator, timeOrigin, componentNames, false);
    }

    /**
     * Constructs an empty namespace, given a list of component names.
     * @param evaluator the expression evaluator with which the constructed
     *   Namespace is to be used 
     * @param timeOrigin origin of simulation time
     * @param componentNames collection of unique component names
     * @param useDecisions whether to initialize the decisions fields.
     *   Should be set true for scenario generation optimization.
     */
    public Namespace(Evaluator evaluator, Instant timeOrigin,
            Collection<String> componentNames, boolean useDecisions) {
        super(evaluator, timeOrigin);
        this.components = new HashMap<>();
        for (String componentName : componentNames) {
            components.put(componentName, new Component(useDecisions));
        }
        this.externals = new HashMap<>();
        this.metrics = new HashMap<>();
        this.decisions = useDecisions ? new HashMap<>() : null;
    }

    /**
     * Sets up the names and types of the parameters in the virtual
     * configuration component.
     */
    public void initConfigComponent() {
        Component component = getOrNew(CONFIG_COMPONENT);
        component.inputs.put(CONFIG_SIMULATION_START, Type.TIMESTAMP);
        component.inputs.put(CONFIG_SIMULATION_END, Type.TIMESTAMP);
    }

    public boolean usesDecisions() {
        return decisions != null;
    }
    
    /** Retrieve or create a component by name.
     *  If no component with the given name exists, create it.
     * 
     * @param name name of the component to return.
     * @return the component corresponding to name.
     */
    public Component getOrNew(String name) {
        return components.computeIfAbsent(
                name, n -> new Component(usesDecisions()));
    }
    
    /**
     * Return the type of an input parameter.
     * @param comp component name
     * @param var parameter name
     * @return the {@link Type} or null if no such input
     */
    public Type getInputType(String comp, String var) {
        Component c = components.get(comp);
        return c != null ? c.inputs.get(var) : null;
    }

    /**
     * Return the type of an output variable.
     * @param comp component name
     * @param var variable name
     * @return the {@link Type} or null if no such output
     */
    public Type getOutputType(String comp, String var) {
        Component c = components.get(comp);
        return c != null ? c.outputs.get(var) : null;
    }

    /**
     * Return the type of a decision variable.
     * @param comp component name, null for top-level
     * @param var variable name
     * @return the {@link Type} or null if no such variable
     */
    public Type getDecisionType(String comp, String var) {
        if (comp == null)
            return decisions.get(var);
        Component c = components.get(comp);
        return c != null ? c.decisions.get(var) : null;
    }
}
