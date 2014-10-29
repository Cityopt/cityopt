package eu.cityopt.sim.eval;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * Container for simulation input data.
 *
 * The hashCode and equals methods consider the equality of all input
 * parameter values, so that the class can be used as a map key.
 *
 * Implements the EvalutionContext interface, providing access to
 * both external parameters and the defined simulation inputs.
 *
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class SimulationInput implements EvaluationContext {
	private Namespace namespace;
	private BindingLayer bindingLayer;

	public SimulationInput(ExternalParameters externalParameters, Bindings values) {
		this.namespace = externalParameters.getNamespace();
		this.bindingLayer = new BindingLayer(
				values, externalParameters.getBindingLayer(),
				namespace.inputs, "input parameter");
	}

	public SimulationInput(ExternalParameters externalParameters) {
		this(externalParameters, new SimpleBindings());
	}

	public Namespace getNamespace() {
		return namespace;
	}

	/** Gets the value of an input parameter. */
	public Object get(String inputName) {
		return bindingLayer.get(inputName);
	}

	/** Sets an input parameter value. */
	public Object put(String inputName, Object value) {
		return bindingLayer.put(inputName, value);
	}

	/** Parses an input parameter value and stores it. */
	public Object putString(String inputName, String value) {
		return bindingLayer.putString(inputName, value);
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
	public Bindings toBindings() {
		return bindingLayer.toBindings();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SimulationInput)) {
			return false;
		} else if (other == this) {
			return true;
		} else {
			return bindingLayer.getLocals().equals(
					((SimulationInput) other).bindingLayer.getLocals());
		}
	}
	
	@Override
	public int hashCode() {
		return bindingLayer.getLocals().hashCode();
	}
}
