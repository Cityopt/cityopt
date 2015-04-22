package eu.cityopt.sim.eval;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.python.core.Py;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;

/**
 * Evaluation engine of the expression language. The expression language is
 * Python, the Java implementation of which is provided by the Jython project.
 * <p>
 * Generally all Python/Jython specific code paths go through Evaluator, and
 * elsewhere we use only the Java Scripting API. The TimeSeries class is a
 * part of the Python interface and is tightly coupled with Evaluator.
 *
 * @see <a href="http://www.jython.org/">http://www.jython.org/</a>
 * @see SyntaxChecker
 * 
 * @author Hannu Rummukainen
 */
public class Evaluator {
    private static final String ENGINE_NAME = "python";

    private static Object initialSetupMutex = new Object();
    private static volatile boolean initialSetupDone = false;

    private static final String PYTHON_IMPORTS =
            "from __future__ import division\n" +
            "import __builtin__, math, cmath, itertools\n" +
            "import cityopt, cityopt.syntax\n" +
            "import datetime as _datetime\n" +
            "from datetime import *\n" +
            "from math import *\n" +
            // built-in pow is preferred to math.pow
            "del pow\n" +
            "from cityopt import *\n";

    /**
     * When the Jython interpreter calls back to Java code, context data is
     * passed behind the back of the Jython interpreter in thread-local storage.
     */
    private static ThreadLocal<EvaluationSetup> activeSetup =
            new ThreadLocal<EvaluationSetup>();

    private ScriptEngine engine;
    private PyObject _convertSimtimesToDatetimes;
    private PyObject _convertToSimtimes;
    private PyObject _convertToSimtime;
    private PyObject _izip;

    public Evaluator() {
        doInitialSetup();

        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName(ENGINE_NAME);
        if (engine == null) {
            throw new RuntimeException("Cannot find scripting engine \""
                    + ENGINE_NAME + "\"");
        }
        if (!(engine instanceof Compilable)) {
            throw new RuntimeException("Scripting engine \"" + ENGINE_NAME
                    + "\" has no compiler");
        }
        if (!(engine instanceof Invocable)) {
            throw new RuntimeException("Scripting engine \"" + ENGINE_NAME
                    + "\" has no Invocable interface");
        }
        String threading = (String) engine.getFactory().getParameter(
                "THREADING");
        if (threading == null) {
            throw new RuntimeException("Scripting engine \"" + ENGINE_NAME
                    + "\" is not multi-threaded");
        }
        try {
            engine.eval(PYTHON_IMPORTS);
    
            _convertSimtimesToDatetimes = getPyObject("cityopt._convertSimtimesToDatetimes");
            _convertToSimtimes = getPyObject("cityopt._convertToSimtimes");
            _convertToSimtime = getPyObject("cityopt._convertToSimtime");
            _izip = getPyObject("itertools.izip");
            // TODO: engine.setContext
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to initialize Python environment", e);
        }
    }

    private static void doInitialSetup() {
        synchronized (initialSetupMutex) {
            if ( ! initialSetupDone) {
                initPythonPath();
                initialSetupDone = true;
            }
        }
    }

    /**
     * Adds our Lib directory to the Python module search path.
     * <p>
     * When the sim-eval module is packaged in a JAR file, a copy of the Python
     * Lib directory is included in the JAR via maven-resources-plugin, and we
     * use that copy in the Python search path. When running directly from class
     * files, which should occur only in JUnit testing, we use the source
     * directory src/main/resources/Lib.
     * <p>
     * Jython authors promote bundling Jython with user code in a single
     * combined JAR file, in which case Jython automatically finds any Python
     * modules in the Lib directory in the common JAR. There is a
     * maven-jython-compile-plugin which automates the bundling process and is
     * also able to include additional Python modules via easy_install. However,
     * there is no plugin solution for running JUnit tests.
     * <p>
     * One point of potential fragility in our solution should be noted: When
     * Maven runs unit tests between the compile and package phases, Jython
     * saves bytecode versions of the Python files in the source directory, and
     * those bytecode files are then copied to the produced JAR.
     */
    private static void initPythonPath() {
        final String PYTHON_PATH = "python.path";
        URL url = Evaluator.class.getResource("/Lib/cityopt/__init__.py");
        try {
            String libraryPath;
            URI uri = url.toURI();
            if (uri.getScheme().equalsIgnoreCase("jar")) {
                JarURLConnection jarUrl = (JarURLConnection) url.openConnection();
                Path jarPath = Paths.get(jarUrl.getJarFileURL().toURI());
                libraryPath = jarPath.resolve(jarUrl.getEntryName()).getParent().getParent().toString();
            } else {
                libraryPath = Paths.get(url.toURI()).getParent().resolve(
                        "../../../../src/main/resources/Lib").normalize().toString();
            }
            String oldLibraryPath = System.getProperty(PYTHON_PATH); 
            if (oldLibraryPath != null) {
                libraryPath = libraryPath + System.getProperty("path.separator") 
                        + oldLibraryPath;
            }
            System.setProperty(PYTHON_PATH, libraryPath.toString());
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException(
                    "Invalid Python resource URL: " + url, e);
        }
    }

    /**
     * Constructs a time series representation for use in expressions. Instances
     * of TimeSeriesI can be put in ExternalParameters and SimulationResults.
     *
     * @param type
     *            determines how to interpolate values between the defined
     *            points. Must be Type.TIMESERIES_STEP or
     *            Type.TIMESERIES_LINEAR.
     * @param times
     *            the defined time points, seconds from simulation time origin.
     *            Must be in ascending order (but non-consecutive vertical
     *            segments are allowed in linear interpolation). Outside the
     *            closed interval from the first to the last time point, values
     *            are assumed to be zero. Usually there should be at least two
     *            points, so that the series covers a non-empty time interval.
     * @param values
     *            the values at the defined time points. It is recommended to
     *            set the last value of a step function as 0.
     * @return a TimeSeriesI instance
     */
    public TimeSeriesI makeTS(Type type, double[] times, double[] values) {
        return new TimeSeries(PiecewiseFunction.make(
                type.getInterpolationDegree(), times, values));
    }

    /**
     * Returns the evaluator running Python code in the current thread.
     * @throws IllegalStateException if no Python code is being run.
     */
    static Evaluator getActiveEvaluator() {
        Evaluator evaluator = activeSetup.get().evaluator;
        if (evaluator == null) {
            throw new IllegalStateException("Expected to be called from Python " +
                    "code, but there is no active Evaluator in this thread.");
        }
        return evaluator;
    }

    /**
     * For internal use only - this is public for technical reasons.
     * Returns the time origin of the project when running Python code.
     */
    public static double getActiveTimeOrigin() {
        return activeSetup.get().originTimestamp;
    }

    PyObject getPyObject(String name) throws ScriptException {
        return (PyObject) engine.eval(name);
    }

    /**
     * Evaluates a script in the default top-level environment.
     * @return result of the script
     */
    public Object eval(String code, EvaluationSetup setup) throws ScriptException {
        if (setup.evaluator != this) {
            throw new IllegalStateException("Evaluator mismatch");
        }
        try {
            activeSetup.set(setup);
            return engine.eval(code);
        } finally {
            activeSetup.set(null);
        }
    }

    /**
     * Evaluates a compiled script.  Sets up the context for the CITYOPT
     * Python support code and then calls CompiledScript.eval.
     * @return result of the script
     */
    Object eval(CompiledScript script, Bindings bindings, EvaluationSetup setup)
            throws ScriptException {
        if (setup.evaluator != this) {
            throw new IllegalStateException("Evaluator mismatch");
        }
        try {
            activeSetup.set(setup);
            return script.eval(bindings);
        } finally {
            activeSetup.set(null);
        }
    }

    /**
     * Access to the script compiler.  To support all CITYOPT functionality,
     * please use Evaluator to evaluate any compiled scripts. 
     * @see #eval(CompiledScript, Bindings)
     */
    Compilable getCompiler() {
        return (Compilable) engine;
    }

    Bindings copyGlobalBindings() {
        Bindings b = engine.createBindings();
        b.putAll(engine.getBindings(ScriptContext.ENGINE_SCOPE));
        return b;
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

    Component makeComponent(String componentName) {
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

    /** Call to cityopt._convertSimtimesToDatetimes for Java called from Jython */
    PyObject convertSimtimesToDatetimes(double[] times) {
        return _convertSimtimesToDatetimes.__call__(
                Py.javas2pys(new Object[] { times }));
    }

    /** Call to cityopt._convertToSimtimes for Java called from Jython */
    double[] convertToSimtimes(PyObject arg) {
        PyObject result = _convertToSimtimes.__call__(arg);
        return (double[]) result.__tojava__(double[].class);
    }

    /** Call to cityopt._convertToSimTime for Java called from Jython */
    double convertToSimtime(PyObject arg) {
        return _convertToSimtime.__call__(arg).asDouble();
    }

    /** Call to itertools.izip for Java called from Jython */
    PyObject izip(double[] times, double[] values) {
        return _izip.__call__(Py.javas2pys(new Object[] { times, values }));
    }

    /** Converts a double to an equivalent Python expression. */
    String toExpression(double value) {
        if (Double.isInfinite(value)) {
            return (value < 0) ? "float('-inf')" : "float('inf')";
        } else if (Double.isNaN(value)) {
            return "float('nan')";
        } else {
            return Double.toString(value);
        }
    }

    /** Converts a string to an equivalent Python expression. */
    String toExpression(String value) {
        return new PyString(value).__repr__().asString();
    }
}
