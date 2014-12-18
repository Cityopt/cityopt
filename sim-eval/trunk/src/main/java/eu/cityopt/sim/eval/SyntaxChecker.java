package eu.cityopt.sim.eval;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;

/**
 * Checks expressions for errors without running them. Can be used to provide
 * feedback at the user interface level, or at the time of starting an
 * optimization run. Provides also features for validating identifier names and
 * converting user-entered names into valid identifiers in the expression
 * language.
 * <p>
 * The expression checking is not complete, i.e. it does not detect all possible
 * errors that could occur when the expressions are used. The checking is
 * focused on the syntax of the expression language, and whether there are
 * references to undefined functions or variables.
 *
 * @author Hannu Rummukainen
 */
public class SyntaxChecker {
    private static final Pattern PYTHON_IDENTIFIER_PATTERN =
            Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*$");

    /**
     * The following global names are considered reserved, so that they can be
     * used without worries in expressions.
     */
    private static final String[] RESERVED_GLOBALS_ARRAY = {
        // Python built-ins
        "False", "None", "True", "abs", "all", "any", "bool", "dict",
        "enumerate", "float", "int", "len", "list", "map", "max", "min",
        "pow", "range", "reduce", "reversed", "round", "set", "sorted",
        "str", "sum", "tuple", "xrange", "zip",
        // from the standard math module
        "acos", "asin", "atan", "atan2", "ceil", "cos", "cosh", "exp",
        "floor", "hypot", "log", "sin", "sinh", "sqrt", "tan", "tanh",
        // from the standard datetime module
        "datetime", "timedelta",
        // from the cityopt module
        "DAY_S", "HOUR_S", "INFINITY", "MINUTE_S", "TimeSeries",
        "integrate", "mean", "stdev", "totimestamp", "var",
        // module names
        "__builtin__", "math", "cmath", "cityopt", "itertools" 
    };
    private static final Set<String> reservedGlobals =
            prepareReservedNames(Arrays.asList(RESERVED_GLOBALS_ARRAY));

    private final Set<String> reservedKeywords;

    private final Evaluator evaluator;
    private final PyObject _checkExpressionSyntax;

    private final PyDictionary environmentWithExternals;
    private final PyDictionary environmentWithDecisions;
    private final PyDictionary environmentWithInputs;
    private final PyDictionary environmentWithResults;
    private final PyDictionary environmentWithMetrics;
    private final boolean environmentsAreComplete;

    /**
     * Constructs an incomplete syntax checker via SyntaxChecker(evaluator, null, false).
     * @see #SyntaxChecker(Evaluator, Namespace, boolean)
     */
    public SyntaxChecker(Evaluator evaluator) throws ScriptException {
        this(evaluator, null, false);
    }

    /**
     * Constructs a syntax checker, optionally with a specific set of defined
     * names.
     *
     * @param evaluator the expression language evaluator object to use
     * @param namespace either a Namespace object specifying the valid names
     *  of parameters and variables in the CITYOPT project, or null, in which
     *  case only global function names are checked.
     * @param namespaceComplete whether the Namespace should be assumed to be 
     *  complete, i.e. whether all parameters and variables are defined there.
     *  A complete namespace allows stricter checking.
     *  Ignored if namespace is null.
     */
    public SyntaxChecker(Evaluator evaluator, Namespace namespace, boolean namespaceComplete)
            throws ScriptException {
        this.evaluator = evaluator;
        if (namespace != null) {
            if (namespace.evaluator != evaluator) {
                throw new IllegalArgumentException("Different evaluator in namespace");
            }
            this.environmentWithExternals = new PyDictionary();
            this.environmentWithDecisions =
                    (namespace.decisions != null) ? new PyDictionary() : null;
            this.environmentWithInputs= new PyDictionary();
            this.environmentWithResults = new PyDictionary();
            this.environmentWithMetrics = new PyDictionary();
            fillEnvironments(namespace);
            this.environmentsAreComplete = namespaceComplete;
        } else {
            PyDictionary globalEnvironment = new PyDictionary();
            globalEnvironment.putAll(evaluator.copyGlobalBindings());
            this.environmentWithExternals = globalEnvironment;
            this.environmentWithDecisions = globalEnvironment;
            this.environmentWithInputs = globalEnvironment;
            this.environmentWithResults = globalEnvironment;
            this.environmentWithMetrics = globalEnvironment;
            this.environmentsAreComplete = false;
        }

        _checkExpressionSyntax = (PyObject) evaluator.eval(
                "cityopt.syntax.checkExpressionSyntax");

        @SuppressWarnings("unchecked")
        List<String> keywordList = 
                (List<String>) evaluator.eval("cityopt.syntax.kwlist");
        reservedKeywords = prepareReservedNames(keywordList);
    }

    private void fillEnvironments(Namespace namespace) throws ScriptException {
        EnumMap<Type, Object> placeholders = makePlaceholders(namespace.evaluator);

        ExternalParameters externals = new ExternalParameters(namespace);
        for (Map.Entry<String, Type> ee : namespace.externals.entrySet()) {
            externals.put(ee.getKey(), placeholders.get(ee.getValue()));
        }
        environmentWithExternals.putAll(externals.toBindings());

        if (environmentWithDecisions != null) {
            DecisionValues decisions = new DecisionValues(externals);
            for (Map.Entry<String, Type> de : namespace.decisions.entrySet()) {
                decisions.put(null, de.getKey(), placeholders.get(de.getValue()));
            }
            for (Map.Entry<String, Namespace.Component> ce : namespace.components.entrySet()) {
                for (Map.Entry<String, Type> de : ce.getValue().decisions.entrySet()) {
                    decisions.put(ce.getKey(), de.getKey(), placeholders.get(de.getValue()));
                }
            }
            environmentWithDecisions.putAll(decisions.toBindings());
        }

        SimulationInput input = new SimulationInput(externals);
        for (Map.Entry<String, Namespace.Component> ce : namespace.components.entrySet()) {
            for (Map.Entry<String, Type> ie : ce.getValue().inputs.entrySet()) {
                input.put(ce.getKey(), ie.getKey(), placeholders.get(ie.getValue()));
            }
        }
        environmentWithInputs.putAll(input.toBindings());

        SimulationResults results = new SimulationResults(input, "");
        for (Map.Entry<String, Namespace.Component> ce : namespace.components.entrySet()) {
            for (Map.Entry<String, Type> oe : ce.getValue().outputs.entrySet()) {
                results.put(ce.getKey(), oe.getKey(), placeholders.get(oe.getValue()));
            }
        }
        environmentWithResults.putAll(results.toBindings());

        List<MetricExpression> metricList = new ArrayList<MetricExpression>();
        for (Map.Entry<String, Type> me : namespace.metrics.entrySet()) {
            metricList.add(new MetricExpression(0, me.getKey(), "1", evaluator));
        }
        try {
            MetricValues metrics = new MetricValues(results, metricList);
            environmentWithMetrics.putAll(metrics.toBindings());
        } catch (InvalidValueException e) {
            // Should not happen since the expressions are simply "1"
            throw new RuntimeException(e);
        }
    }

    private static EnumMap<Type, Object> makePlaceholders(Evaluator evaluator) {
        EnumMap<Type, Object> placeholders = new EnumMap<Type, Object>(Type.class);
        for (Type type : Type.values()) {
            Object p;
            switch (type) {
            case DOUBLE:
                p = Double.valueOf(1.0);
                break;
            case INTEGER:
                p = Integer.valueOf(1);
                break;
            case STRING:
                p = "";
                break;
            case TIMESERIES_LINEAR:
            case TIMESERIES_STEP:
                p = evaluator.makeTS(type, new double[] { 0.0 }, new double[] { 1.0 });
                break;
            case LIST_OF_INTEGER:
                p = Arrays.asList(1);
                break;
            case LIST_OF_DOUBLE:
                p = Arrays.asList(1.0);
                break;
            default:
                throw new IllegalStateException("No placeholder defined for "+type);
            }
            placeholders.put(type, p);
        }
        return placeholders;
    }

    private static Set<String> prepareReservedNames(Collection<String> names) {
        Set<String> nameSet = new HashSet<String>();
        nameSet.addAll(names);
        // For each name of the form XX__, add the prefixes XX_ and XX
        // so that underscore normalization works more predictably.
        for (String name : names) {
            while (name.endsWith("_")) {
                name = name.substring(0, name.length() - 1);
                nameSet.add(name);
            }
        }
        return nameSet;
    }

    /**
     * Whether the given string is a valid top level name in CITYOPT
     * expressions.
     * @see #normalizeTopLevelName(String)
     */
    public boolean isValidTopLevelName(String name) {
        return PYTHON_IDENTIFIER_PATTERN.matcher(name).matches()
                && !reservedKeywords.contains(name)
                && !reservedGlobals.contains(name);
    }

    /**
     * Whether the given string is a valid attribute name for CITYOPT
     * components.
     * @see #normalizeAttributeName(String)
     */
    public boolean isValidAttributeName(String name) {
        return PYTHON_IDENTIFIER_PATTERN.matcher(name).matches()
                && !reservedKeywords.contains(name);
    }

    /**
     * Converts an arbitrary string into a valid top level name in CITYOPT
     * expressions.  Does the same conversions as normalizeIdentifierName,
     * and then avoids reserved global names.
     * <p>
     * Many different inputs can give the same result: this method does nothing
     * to avoid clashes between user-defined names.
     */
    public String normalizeTopLevelName(String unicodeName) {
        String name = normalizeAttributeName(unicodeName);
        while (reservedGlobals.contains(name)) {
            name = name + "_";
        }
        return name;
    }

    /**
     * Converts an arbitrary string into a valid Python identifier. Diacritical
     * marks are removed (e.g. ä -> a) and otherwise invalid characters are
     * converted to underscores. If the result is a Python keyword, underscores
     * are appended.
     * <p>
     * In particular the result is a valid attribute name for CITYOPT
     * components.
     * <p>
     * Many different inputs can give the same result: this method does nothing
     * to avoid clashes between user-defined names.
     */
    public String normalizeAttributeName(String unicodeName) {
        // Represent diacritical marks with combining characters, e.g. ä -> a¨
        String n = Normalizer.normalize(unicodeName, Normalizer.Form.NFKD);
        // Drop diacritics and convert invalid identifier characters to underscores.
        n = n.replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+", "")
                .replaceAll("[^0-9a-zA-Z_]", "_");
        // Prepend an underscore if the name is not yet valid.
        if (n.isEmpty() || n.matches("^[0-9]")) {
            n = "_" + n;
        }
        // Avoid reserved keywords.
        while (reservedKeywords.contains(n)) {
            n = n + "_";
        }
        return n.equals(unicodeName) ? unicodeName : n;
    }

    /**
     * An error message related to a Python expression.
     */
    public static class Error {
        /** Line number starting from 0. */
        public final int line;

        /** Column number starting from 0. */
        public final int column;

        /** Human-readable error message. */
        public final String message;

        public Error(int line, int column, String message) {
            this.line = line;
            this.column = column;
            this.message = message;
        }
    }

    /**
     * Checks for errors in an expression that is evaluated using
     * external parameters only.  Such expressions could be used for
     * defining the range of a decision variable.
     * @param source the expression text
     * @return null if no errors are detected, otherwise an error message
     */
    public Error checkExternalExpression(String source) {
        return checkExpressionSyntax(source, environmentWithExternals);
    }

    /**
     * Checks for errors in an expression defining an input variable.
     * Input expressions are only used in scenario generation optimization.
     * @param source the expression text
     * @return null if no errors are detected, otherwise an error message
     * @throws IllegalStateException if the namespace does not have
     *   decision variables.
     */
    public Error checkInputExpression(String source) {
        if (environmentWithDecisions == null) {
            throw new IllegalStateException(
                    "Cannot check input expressions because decision variables have not been defined");
        }
        return checkExpressionSyntax(source, environmentWithDecisions);
    }

    /**
     * Checks for errors in an optimization constraint expression THAT IS
     * EVALUATED BEFORE SIMULATION.
     * @param source the expression text
     * @return null if no errors are detected, otherwise an error message
     * @see #checkConstraintExpression(String)
     */
    public Error checkPreConstraintExpression(String source) {
        return checkExpressionSyntax(source, environmentWithInputs);
    }

    /**
     * Checks for errors in an expression defining a metric. 
     * @param source the expression text
     * @return null if no errors are detected, otherwise an error message
     */
    public Error checkMetricExpression(String source) {
        return checkExpressionSyntax(source, environmentWithResults);
    }

    /**
     * Checks for errors in an optimization constraint expression.
     * Assumes that the constraint is evaluated after simulation, so that
     * simulation results are available.
     *
     * @param source the expression text
     * @return null if no errors are detected, otherwise an error message
     */
    public Error checkConstraintExpression(String source) {
        return checkExpressionSyntax(source, environmentWithMetrics);
    }

    /**
     * Checks for errors in an objective function expression.
     *
     * @param source the expression text
     * @return null if no errors are detected, otherwise an error message
     */
    public Error checkObjectiveExpression(String source) {
        return checkExpressionSyntax(source, environmentWithMetrics);
    }

    /** Calls the Python function cityopt.syntax.checkExpressionSyntax. */
    private Error checkExpressionSyntax(String source, PyDictionary environment) {
        PyObject o = _checkExpressionSyntax.__call__(
                Py.javas2pys(new Object[] { 
                        source, environment, environmentsAreComplete }));
        if (o == Py.None) {
            return null;
        } else {
            return new Error(Math.max(0, o.__getitem__(0).asIndex() - 1),
                    o.__getitem__(1).asIndex(), o.__getitem__(2).asString());
        }
    }
}
