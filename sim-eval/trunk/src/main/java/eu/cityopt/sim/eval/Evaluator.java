package eu.cityopt.sim.eval;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.python.core.Py;
import org.python.core.PyObject;

/**
 * Evaluation engine of the expression language. The expression language is
 * Python, the Java implementation of which is provied by the Jython project.
 * All Python/Jython specific code is kept inside this class; elsewhere we use
 * only the Java Scripting API.
 *
 * @see http://www.jython.org/
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class Evaluator {
    private ScriptEngine engine;

    private static final String ENGINE_NAME = "python";

    public Evaluator() throws EvaluationException, ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName(ENGINE_NAME);
        if (engine == null) {
            throw new EvaluationException("Cannot find scripting engine \""
                    + ENGINE_NAME + "\"");
        }
        if (!(engine instanceof Compilable)) {
            throw new EvaluationException("Scripting engine \"" + ENGINE_NAME
                    + "\" has no compiler");
        }
        if (!(engine instanceof Invocable)) {
            throw new EvaluationException("Scripting engine \"" + ENGINE_NAME
                    + "\" has no Invocable interface");
        }
        String threading = (String) engine.getFactory().getParameter(
                "THREADING");
        if (threading == null) {
            throw new EvaluationException("Scripting engine \"" + ENGINE_NAME
                    + "\" is not multi-threaded");
        }
        // TODO: engine.setContext
    }

    Compilable getCompiler() {
        return (Compilable) engine;
    }

    Invocable getInvoker() {
        return (Invocable) engine;
    }

    Bindings makeTopLevelBindings() {
        return engine.createBindings();
    }

    Bindings makeAttributeBindings() {
        return new SimpleBindings();
    }

    /** Access to a scripting language object representing a component. */
    interface Component {
        /** Gets a reference to the scripting language object. */
        Object getScriptObject();

        /** Sets the attributes of the scripting language object. */
        void setAttributes(Bindings attributeBindings);
    }

    Component makeComponent(String componentName) throws ScriptException {
        final ComponentImpl component = new ComponentImpl();
        final Map<String, PyObject> attributes = component.attributes;

        return new Component() {
            public Object getScriptObject() {
                return component;
            }

            public void setAttributes(Bindings attributeBindings) {
                for (Map.Entry<String, Object> entry : attributeBindings.entrySet()) {
                    attributes.put(entry.getKey(), Py.java2py(entry.getValue()));
                }
            }
        };
    }

    /**
     * Jython object that represents a Cityopt model component. Scripts are not
     * allowed to modify attribute values.
     */
    @SuppressWarnings("serial")
    private static class ComponentImpl extends PyObject {
        protected Map<String, PyObject> attributes = new HashMap<String, PyObject>();

        @Override
        public PyObject __findattr_ex__(String name) {
            return attributes.get(name);
        }

        @Override
        public void __setattr__(String name, PyObject value) {
            super.readonlyAttributeError(name);
        }

        @Override
        public void __delattr__(String name) {
            super.readonlyAttributeError(name);
        }
    }
}
