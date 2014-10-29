package eu.cityopt.sim.eval;

import javax.script.Bindings;
import javax.script.SimpleBindings;

public class ExternalParameters implements EvaluationContext {
	private Namespace namespace;
	private BindingLayer bindingLayer;

	public ExternalParameters(Namespace namespace, Bindings values) {
		this.namespace = namespace;
		this.bindingLayer = new BindingLayer(values, null,
				namespace.externals, "external parameter");
	}

	public ExternalParameters(Namespace namespace) {
		this(namespace, new SimpleBindings());
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public Object get(String externalName) {
		return bindingLayer.get(externalName);
	}

	public Object put(String externalName, Object value) {
		return bindingLayer.put(externalName, value);
	}

	@Override
	public Bindings toBindings() {
		return bindingLayer.toBindings();
	}

	@Override
	public BindingLayer getBindingLayer() {
		return bindingLayer;
	}

}
