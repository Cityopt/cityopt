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
import org.python.core.PyList;
import org.python.core.PyObject;

/**
 * Evaluation engine of the expression language. The expression language is
 * Python, the Java implementation of which is provided by the Jython project.
 *
 * Generally all Python/Jython specific code paths go through Evaluator, and
 * elsewhere we use only the Java Scripting API. The TimeSeriesImpl class is a
 * part of the Python interface and is tightly coupled with Evaluator.
 *
 * @see http://www.jython.org/
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class Evaluator {
    private ScriptEngine engine;

    private static final String ENGINE_NAME = "python";

    private static final String initializationCode =
            "from datetime import datetime, timedelta\n" +
            "def convertTimeMillisToDatetimes(timeMillis):\n" +
            "  return [datetime.fromtimestamp(0.001 * t) for t in timeMillis]\n";

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
        engine.eval(initializationCode);
        // TODO: engine.setContext
    }

    /**
     * Constructs a time series representation for use in expressions.
     * Instances of TimeSeries can be put in ExternalParameters and SimulationResults.
     *
     * @param timeMillis the defined time points, milliseconds since 1 January 1970
     * @param values the values at the defined time points
     * @return a TimeSeries implementation that must be used with this Evaluator
     */
    public TimeSeries makeTimeSeries(long[] timeMillis, double[] values) {
        return new TimeSeriesImpl(this, timeMillis, values);
    }

    /**
     * Constructs an empty time series representation for use in expressions.
     * The time points and associated values must be filled in by the caller.
     * Instances of TimeSeries can be put in ExternalParameters and SimulationResults.
     *
     * @param n number of points to allocate.
     * @return a TimeSeries implementation that must be used with this Evaluator
     */
    public TimeSeries makeTimeSeries(int n) {
        return makeTimeSeries(new long[n], new double[n]);
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
                if (attributeBindings != null) {
                    for (Map.Entry<String, Object> entry : attributeBindings.entrySet()) {
                        attributes.put(entry.getKey(), Py.java2py(entry.getValue()));
                    }
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
        
        @Override
        public PyObject __dir__() {
            return new PyList(attributes.keySet());
        }
    }

    /**
     * Invokes a Python function that is known to exist.  Intended to be
     * called from inside Python evaluation: any ScriptException from the
     * invocation is unwrapped to re-throw the original Python Exception.
     * Also converts NoSuchMethodException into IllegalStateException.
     */
    Object invokeInternal(String name, Object... args) throws Throwable {
        try {
            return getInvoker().invokeFunction(name, args);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "The Python function \"" + name + "\" disappeared", e);
        } catch (ScriptException e) {
            throw (e.getCause() != null) ? e.getCause() : e;
        }
    }
}
