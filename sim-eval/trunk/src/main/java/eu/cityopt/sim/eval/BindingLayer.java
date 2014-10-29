package eu.cityopt.sim.eval;

import java.util.Collections;
import java.util.Map;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * Internal class for implementing layered bindings (environments).
 * Note that the Java scripting API only requires two global scopes,
 * and does not have direct support for Bindings in inner scopes.
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
class BindingLayer {
	private Bindings locals;
	private BindingLayer parent;
	private Bindings merged;
	private Map<String, Type> validTypes;
	private String kindOfObject;

	BindingLayer(Bindings locals, BindingLayer parent,
			Map<String, Type> validTypes, String kindOfObject) {
		if (locals == null) {
			throw new NullPointerException();
		}
		for (Map.Entry<String, Object> entry : locals.entrySet()) {
			validate(entry.getKey(), entry.getValue());
		}
		this.locals = locals;
		this.parent = parent;
		this.validTypes = validTypes;
		this.kindOfObject = kindOfObject;
	}

	public Bindings getLocals() {
		return locals;
	}

	Bindings toBindings() {
		if (merged == null) {
			if (parent == null) {
				merged = unmodifiableBindings(locals);
			} else if (locals.isEmpty()) {
				merged = parent.toBindings();
			} else {
				Bindings b = new SimpleBindings();
				parent.mergeTo(b);
				b.putAll(locals);
				merged = unmodifiableBindings(b);
			}
		}
		return merged;
	}

	static private Bindings unmodifiableBindings(Bindings b) {
		return new SimpleBindings(Collections.unmodifiableMap(b));		
	}

	private void mergeTo(Bindings target) {
		if (merged != null) {
			target.putAll(merged);
		} else {
			if (parent != null) {
				parent.mergeTo(target);
			}
			target.putAll(locals);
		}
	}

	/** Gets the value associated with a name on this layer. */
	Object get(String name) {
		validateName(name);
		return locals.get(name);
	}

	/** 
	 * Sets a value.
	 * Invalidates bindings returned earlier by toBindings.
	 * @param name a valid name on this layer
	 * @param value new value for the name
	 * @return old value associated with the name, or null
	 */
	Object put(String name, Object value) {
		validate(name, value);
		merged = null;
		return locals.put(name, value);
	}

	/** 
	 * Parses a value from a string and stores it.
	 * Invalidates bindings returned earlier by toBindings.
	 * @param name a valid name on this layer
	 * @param value String containing a new value for the name
	 * @return old value associated with the name, or null
	 */
	Object putString(String name, String value) {
		Type type = validateName(name);
		Object object = type.parse(value);
		merged = null;
		return locals.put(name, object);
	}


	private Type validateName(String name) {
		Type type = validTypes.get(name);
		if (type == null) {
			throw new IllegalArgumentException(
					"Unknown " + kindOfObject + " \"" + name + "\"");
		}
		return type;
	}

	private void validate(String name, Object value) {
		Type type = validateName(name);
		if ( ! type.isInstance(value)) {
			throw new IllegalArgumentException(
					"Invalid value for " + name + ": " + value);
		}
	}

	/** Whether all names on this layer have been given a value. */
	boolean isComplete() {
		for (String name : validTypes.keySet()) {
			if (! locals.containsKey(name)) {
				return false;
			}
		}
		return true;
	}
}
