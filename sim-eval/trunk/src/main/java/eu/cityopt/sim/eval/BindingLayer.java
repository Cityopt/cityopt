package eu.cityopt.sim.eval;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptException;

/**
 * Internal class for implementing layered bindings (environments).
 * 
 * Note that the Java scripting API only requires two global scopes, and does
 * not have direct support for Bindings in inner scopes.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
class BindingLayer {
    private final Namespace namespace;
    private final BindingLayer parent;
    private final ComponentNamespaces componentNamespaces;
    private final String kindOfObject;

    /** Local bindings per component, with top-level bindings at null key. */
    private final Map<Object, Bindings> localBindings;

    /** Flattened bindings per component, with top-level bindings at null key. */
    private Map<Object, Bindings> mergedBindings;

    private Bindings evaluationBindings;

    interface ComponentNamespaces {
        Map<String, Type> get(Object componentKey);
    }

    BindingLayer(Namespace namespace, BindingLayer parent,
            ComponentNamespaces componentNamespaces, String kindOfObject) {
        if (parent != null && namespace != parent.namespace) {
            throw new IllegalArgumentException(
                    "BindingLayer namespace mismatch");
        }
        this.namespace = namespace;
        this.parent = parent;
        this.componentNamespaces = componentNamespaces;
        this.kindOfObject = kindOfObject;
        this.localBindings = new HashMap<Object, Bindings>();
        this.localBindings.put(
                null, getEvaluator().makeTopLevelBindings());
        for (Object componentKey : namespace.components.keySet()) {
            if (componentNamespaces.get(componentKey) != null) {
                this.localBindings.put(
                        componentKey, getEvaluator().makeAttributeBindings());
            }
        }
    }

    /**
     * Copy constructor.  The bindings on this layer can be modified
     * independently from the source object, but the parent layer is shared.
     * To begin with, the binding value objects are shared as well.
     */
    BindingLayer(BindingLayer other) {
        this.namespace = other.namespace;
        this.parent = other.parent;
        this.componentNamespaces = other.componentNamespaces;
        this.kindOfObject = other.kindOfObject;
        this.localBindings = new HashMap<Object, Bindings>();
        for (Map.Entry<Object, Bindings> entry : other.localBindings.entrySet()) {
            Bindings b = (entry.getKey() == null)
                    ? getEvaluator().makeTopLevelBindings()
                    : getEvaluator().makeAttributeBindings();
            b.putAll(entry.getValue());
            this.localBindings.put(entry.getKey(), b);
        }
    }

    Evaluator getEvaluator() {
        return namespace.evaluator;
    }

    Namespace getNamespace() {
        return namespace;
    }

    /** Returns the bindings of this layer only. */
    Map<Object, Bindings> getLocalBindings() {
        return localBindings;
    }

    /**
     * Creates top-level bindings for expression evaluation. Component names are
     * bound to special objects that contain component-level bindings. All names
     * bound on this layer, the parent layer, the parent's parent etc. are
     * included at the appropriate scopes.
     * @throws ScriptException 
     */
    Bindings toBindings() throws ScriptException {
        if (evaluationBindings == null) {
            Bindings newEvaluationBindings = getEvaluator().makeTopLevelBindings();

            // Merge the per-component bindings from binding layers, and bind
            // component objects in our new top level environment.
            Map<Object, Bindings> merge = new HashMap<Object, Bindings>();
            for (Object componentKey : namespace.components.keySet()) {
                Evaluator.Component proxy = getEvaluator().makeComponent(componentKey);
                newEvaluationBindings.put(componentKey.toString(), proxy.getScriptObject());
                Bindings b = mergeBindings(componentKey, null);
                if (b != null) {
                    merge.put(componentKey, b);
                }
                proxy.setAttributes(b);
            }
            // Merge the top level bindings from binding layers.
            Bindings forTopLevel = mergeBindings(null, null);

            // Cache the merged bindings.  Component objects are not cached.
            merge.put(null,  forTopLevel);
            mergedBindings = merge;

            // Finish the top level environment for evaluation, by binding
            // all top-level names from this and parent layers.
            newEvaluationBindings.putAll(forTopLevel);
            evaluationBindings = newEvaluationBindings;
        }
        return evaluationBindings;
    }

    private Bindings mergeBindings(Object componentKey, Bindings target) {
        if (mergedBindings != null) {
            Bindings b = mergedBindings.get(componentKey);
            if (target != null) {
                if (b != null) {
                    target.putAll(b);
                }
                return target;
            } else {
                return b;
            }
        } else if (parent == null) {
            Bindings locals = localBindings.get(componentKey);
            if (target != null) {
                if (locals != null) {
                    target.putAll(locals);
                }
                return target;
            } else {
                return locals;
            }
        } else {
            Bindings locals = localBindings.get(componentKey);
            if (locals == null || locals.isEmpty()) {
                return parent.mergeBindings(componentKey, target);
            } else {
                if (target == null) {
                    target = (componentKey == null)
                            ? getEvaluator().makeTopLevelBindings()
                            : getEvaluator().makeAttributeBindings();
                }
                parent.mergeBindings(componentKey, target);
                target.putAll(locals);
                return target;
            }
        }
    }

    /** Gets the value associated with a name on this layer. */
    Object get(Object componentKey, String name) {
        validate(componentKey, name);
        return localBindings.get(componentKey).get(name);
    }

    /** Gets a value from this layer, formatted as a String. */
    String getString(Object componentKey, String name) {
        Type type = validate(componentKey, name);
        return type.format(localBindings.get(componentKey).get(name));
    }

    /**
     * Stores a value. Invalidates bindings returned earlier by toBindings.
     * 
     * @param componentKey
     *            component key object, or null for top-level
     * @param name
     *            a valid name on this layer
     * @param value
     *            new value for the name
     * @return old value associated with the name, or null
     */
    Object put(Object componentKey, String name, Object value) {
        Type type = validate(componentKey, name);
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Invalid value for "
                    + formatReference(componentKey, name) + ": " + value);
        }
        mergedBindings = null;
        return localBindings.get(componentKey).put(name, value);
    }

    /**
     * Parses a value from a string and stores it. Invalidates bindings returned
     * earlier by toBindings.
     * 
     * @param componentKey
     *            component key object, or null for top-level
     * @param name
     *            a valid name on this layer
     * @param value
     *            String containing a new value for the name
     * @return old value associated with the name, or null
     */
    Object putString(Object componentKey, String name, String value) {
        Type type = validate(componentKey, name);
        Object object = type.parse(value);
        mergedBindings = null;
        return localBindings.get(componentKey).put(name, object);
    }

    private Type validate(Object componentKey, String name) {
        Map<String, Type> nameToType = componentNamespaces.get(componentKey);
        if (nameToType == null) {
            if (namespace.components.containsKey(componentKey)) {
                throw new IllegalArgumentException("Cannot access "
                        + formatReference(componentKey));
            } else {
                throw new IllegalArgumentException("Unknown component: "
                        + componentKey);
            }
        }
        Type type = nameToType.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Unknown "
                    + formatReference(componentKey) + ": " + name);
        }
        return type;
    }

    private String formatReference(Object componentKey) {
        if (componentKey == null) {
            return kindOfObject + " at top level";
        } else {
            return kindOfObject + " in component " + componentKey;
        }
    }

    private String formatReference(Object componentKey, String name) {
        if (componentKey == null) {
            return kindOfObject + " " + name + " at top level";
        } else {
            return kindOfObject + " " + name + " in component " + componentKey;
        }
    }

    /** Whether all names on this layer have been given a value. */
    boolean isComplete() {
        for (Object componentKey : namespace.components.keySet()) {
            Map<String, Type> nameToType = componentNamespaces
                    .get(componentKey);
            if (nameToType != null) {
                Bindings locals = localBindings.get(componentKey);
                for (String name : nameToType.keySet()) {
                    if (!locals.containsKey(name)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        String delim = " ";
        for (Map.Entry<Object, Bindings> entry : localBindings.entrySet()) {
            String componentName = (entry.getKey() != null) ? entry.getKey().toString() : null;
            for (Map.Entry<String, Object> binding : entry.getValue().entrySet()) {
                sb.append(delim);
                if (componentName != null) {
                    sb.append(componentName);
                    sb.append('.');
                }
                sb.append(binding.getKey().toString());
                sb.append(" = ");
                sb.append(binding.getValue().toString());
                delim = ", ";
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}
