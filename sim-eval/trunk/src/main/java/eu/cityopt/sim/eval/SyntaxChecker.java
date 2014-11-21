package eu.cityopt.sim.eval;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;

/**
 * Checks expressions for errors without running them. Can be used to provide
 * feedback at the user interface level, or at the time of starting an
 * optimization run.
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
        "False", "None", "True", "abs", "all", "any", "bool", "cmp", "dict",
        "enumerate", "filter", "float", "int", "len", "list", "map", "max",
        "min", "pow", "range", "reduce", "reversed", "round", "set", "sorted",
        "str", "sum", "tuple", "xrange", "zip",
        // from the standard math module
        "acos", "acosh", "asin", "asinh", "atan", "atanh", "atan2", "ceil",
        "cos", "cosh", "e", "erf", "erfc", "exp", "expm1", "floor", "gamma",
        "hypot", "isinf", "isnan", "lgamma", "log", "log1p", "log10", "pi",
        "sin", "sinh", "sqrt", "tan", "tanh",
        // from the standard datetime module
        "datetime", "timedelta",
        // from the cityopt module
        "integrate", "mean", "stdev", "var",
        // module names
        "__builtin__", "math", "cmath", "cityopt",
        "itertools", "functools" 
    };
    private static final Set<String> reservedGlobals =
            prepareReservedNames(Arrays.asList(RESERVED_GLOBALS_ARRAY));

    private final Evaluator evaluator;
    private final PyObject _checkExpressionSyntax;

    private final Set<String> reservedKeywords;

    public SyntaxChecker(Evaluator evaluator) throws ScriptException {
        this.evaluator = evaluator;
        _checkExpressionSyntax = (PyObject) evaluator.eval(
                "cityopt.syntax.checkExpressionSyntax");

        @SuppressWarnings("unchecked")
        List<String> keywordList = 
                (List<String>) evaluator.eval("cityopt.syntax.kwlist");
        reservedKeywords = prepareReservedNames(keywordList);
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
    public static class ErrorMessage {
        /** Line number starting from 0. */
        public final int line;

        /** Column number starting from 0. */
        public final int column;

        /** Human-readable error message. */
        public final String message;

        public ErrorMessage(int line, int column, String message) {
            this.line = line;
            this.column = column;
            this.message = message;
        }
    }

    /**
     * Checks the syntax of an expression and optionally any identifier
     * references.  Possible parameter and variable values are not considered,
     * and thus the check will not find all possible runtime failures.
     * 
     * @param source  the expression to check
     * @param context either a valid evaluation context associating names
     *  with values, or null, in which case only global function names are
     *  checked.
     * @param complete whether the context is complete, i.e. no additional
     *  names will be defined before the expression is evaluated.
     *  Ignored if context is null.
     * @return null if there are no errors detected; otherwise an
     *  ErrorMessage describing the detected problem.
     */
    public ErrorMessage checkExpressionSyntax(
            String source, EvaluationContext context, boolean complete) {
        try {
            PyDictionary env = new PyDictionary();
            if (context != null) {
                env.putAll(context.toBindings());
            } else {
                env.putAll(evaluator.makeTopLevelBindings());
                complete = false;
            }
            PyObject o = _checkExpressionSyntax.__call__(
                    Py.javas2pys(new Object[] { source, env, complete }));
            if (o == Py.None) {
                return null;
            } else {
                return new ErrorMessage(o.__getitem__(0).asIndex(),
                        o.__getitem__(1).asIndex(), o.__getitem__(2).asString());
            }
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
