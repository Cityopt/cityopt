package eu.cityopt.sim.eval;

import java.text.ParseException;
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
 * @author Hannu Rummukainen
 */
class BindingLayer {
    private final Namespace namespace;
    private final BindingLayer parent;
    private final ComponentNamespaces componentNamespaces;
    private final String kindOfObject;

    /** Local bindings per component, with top-level bindings at null key. */
    private final Map<String, Bindings> localBindings;

    /** Flattened bindings per component, with top-level bindings at null key. */
    private Map<String, Bindings> mergedBindings;

    private Bindings evaluationBindings;

    interface ComponentNamespaces {
        Map<String, Type> get(String componentName);
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
        this.localBindings = new HashMap<String, Bindings>();
        this.localBindings.put(
                null, getEvaluator().makeTopLevelBindings());
        for (String componentName : namespace.components.keySet()) {
            if (componentNamespaces.get(componentName) != null) {
                this.localBindings.put(
                        componentName, getEvaluator().makeAttributeBindings());
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
        this.localBindings = new HashMap<String, Bindings>();
        for (Map.Entry<String, Bindings> entry : other.localBindings.entrySet()) {
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

    /**
     * Creates top-level bindings for expression evaluation. Component names are
     * bound to special objects that contain component-level bindings. All names
     * bound on this layer, the parent layer, the parent's parent etc. are
     * included at the appropriate scopes.
     * @throws ScriptException 
     */
    Bindings toBindings() throws ScriptException {
        if (evaluationBindings == null) {
            Bindings newEvaluationBindings = getEvaluator().copyGlobalBindings();

            // Merge the per-component bindings from binding layers, and bind
            // component objects in our new top level environment.
            Map<String, Bindings> merge = new HashMap<String, Bindings>();
            for (String componentName : namespace.components.keySet()) {
                Evaluator.Component proxy = getEvaluator().makeComponent(componentName);
                newEvaluationBindings.put(componentName.toString(), proxy.getScriptObject());
                Bindings b = mergeBindings(componentName, null);
                if (b != null) {
                    merge.put(componentName, b);
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

    private Bindings mergeBindings(String componentName, Bindings target) {
        if (mergedBindings != null) {
            Bindings b = mergedBindings.get(componentName);
            if (target != null) {
                if (b != null) {
                    target.putAll(b);
                }
                return target;
            } else {
                return b;
            }
        } else if (parent == null) {
            Bindings locals = localBindings.get(componentName);
            if (target != null) {
                if (locals != null) {
                    target.putAll(locals);
                }
                return target;
            } else {
                return locals;
            }
        } else {
            Bindings locals = localBindings.get(componentName);
            if (locals == null || locals.isEmpty()) {
                return parent.mergeBindings(componentName, target);
            } else {
                if (target == null) {
                    target = (componentName == null)
                            ? getEvaluator().makeTopLevelBindings()
                            : getEvaluator().makeAttributeBindings();
                }
                parent.mergeBindings(componentName, target);
                target.putAll(locals);
                return target;
            }
        }
    }

    /** Gets the value associated with a name on this layer. */
    Object get(String componentName, String name) {
        validate(componentName, name);
        return localBindings.get(componentName).get(name);
    }

    /** Gets a value from this layer, formatted as a String. */
    String getString(String componentName, String name) {
        Type type = validate(componentName, name);
        return type.format(localBindings.get(componentName).get(name));
    }

    /**
     * Stores a value. Invalidates bindings returned earlier by toBindings.
     * 
     * @param componentName
     *            component name, or null for top-level
     * @param name
     *            a valid name on this layer
     * @param value
     *            new value for the name
     * @return old value associated with the name, or null
     */
    Object put(String componentName, String name, Object value) {
        Type type = validate(componentName, name);
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Invalid value for "
                    + formatReference(componentName, name) + ": " + value);
        }
        mergedBindings = null;
        evaluationBindings = null;
        return localBindings.get(componentName).put(name, value);
    }

    /**
     * Parses a value from a string and stores it. Invalidates bindings returned
     * earlier by toBindings.
     * 
     * @param componentName
     *            component name, or null for top-level
     * @param name
     *            a valid name on this layer
     * @param value
     *            String containing a new value for the name
     * @return old value associated with the name, or null
     * @throws ParseException if the string cannot be parsed as the correct type
     */
    Object putString(String componentName, String name, String value)
            throws ParseException {
        Type type = validate(componentName, name);
        Object object = type.parse(value);
        mergedBindings = null;
        evaluationBindings = null;
        return localBindings.get(componentName).put(name, object);
    }

    private Type validate(String componentName, String name) {
        Map<String, Type> nameToType = componentNamespaces.get(componentName);
        if (nameToType == null) {
            if (namespace.components.containsKey(componentName)) {
                throw new IllegalArgumentException("Cannot access "
                        + formatReference(componentName));
            } else {
                throw new IllegalArgumentException("Unknown component: "
                        + componentName);
            }
        }
        Type type = nameToType.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Unknown "
                    + formatReference(componentName) + ": " + name);
        }
        return type;
    }

    private String formatReference(String componentName) {
        if (componentName == null) {
            return kindOfObject + " at top level";
        } else {
            return kindOfObject + " in component " + componentName;
        }
    }

    private String formatReference(String componentName, String name) {
        if (componentName == null) {
            return kindOfObject + " " + name + " at top level";
        } else {
            return kindOfObject + " " + name + " in component " + componentName;
        }
    }

    /** Whether all names on this layer have been given a value. */
    boolean isComplete() {
        for (String componentName : namespace.components.keySet()) {
            Map<String, Type> nameToType = componentNamespaces.get(componentName);
            if (nameToType != null) {
                Bindings locals = localBindings.get(componentName);
                for (String name : nameToType.keySet()) {
                    if (!locals.containsKey(name)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Computes a hash code for the bindings on this layer.
     * This is a work-around for a JDK bug: SimpleBindings does not implement hashcode.
     */
    public int localHashCode() {
        int h = 0;
        for (Map.Entry<String, Bindings> entry : localBindings.entrySet()) {
            String key = entry.getKey();
            int i = 0;
            for (Map.Entry<String, Object> binding : entry.getValue().entrySet()) {
                i += binding.hashCode();
            }
            h += i ^ ((key == null) ? 0 : entry.getKey().hashCode());
        }
        return h;
    }

    /**
     * Whether all names on this layer have the same values as in the other instance.
     * This is a work-around for a JDK bug: SimpleBindings does not implement equals.
     */
    public boolean localBindingsEqual(BindingLayer other) {
        if (localBindings.size() != other.localBindings.size()) {
            return false;
        }
        try {
            for (Map.Entry<String, Bindings> entry : localBindings.entrySet()) {
                Bindings thisBindings = entry.getValue();
                Bindings otherBindings = other.localBindings.get(entry.getKey());
                if (thisBindings.size() != otherBindings.size()) {
                    return false;
                }
                for (Map.Entry<String, Object> thisBinding : thisBindings.entrySet()) {
                    String name = thisBinding.getKey();
                    if ( ! thisBinding.getValue().equals(otherBindings.get(name))) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        String delim = " ";
        for (Map.Entry<String, Bindings> entry : localBindings.entrySet()) {
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
