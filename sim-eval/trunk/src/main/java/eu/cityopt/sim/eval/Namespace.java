package eu.cityopt.sim.eval;

import java.util.HashMap;
import java.util.Map;

/**
 * Specifies the named parameters, variables and metrics that can be used in expressions.
 * This is a per-project object: only the names and types are defined here.
 * The actual parameter values may vary in different simulation runs.
 *
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class Namespace {
	/**
	 * Constant parameters external to the simulation (ExtParam).
	 * Map from parameter name to type.
	 */
	public Map<String, Type> externals;

	/**
	 * Simulation model input parameters.
	 * Map from parameter name to type.
	 */
	public Map<String, Type> inputs;

	/**
	 * Simulation model output variables.
	 * Map from variable name to type.
	 */
	public Map<String, Type> outputs;

	/**
	 * Metrics derived from simulation inputs, outputs and external parameters.
	 * Map from metric name to type.
	 */
	public Map<String, Type> metrics;

	/** Constructs an empty namespace. */
	public Namespace() {
		this.externals = new HashMap<String, Type>();
		this.inputs = new HashMap<String, Type>();
		this.outputs = new HashMap<String, Type>();
		this.metrics = new HashMap<String, Type>();
	}

	public Namespace(Map<String, Type> extParameters, Map<String, Type> inputs,
			Map<String, Type> results, Map<String, Type> metrics) {
		this.externals = extParameters;
		this.inputs = inputs;
		this.outputs = results;
		this.metrics = metrics;
	}
}
