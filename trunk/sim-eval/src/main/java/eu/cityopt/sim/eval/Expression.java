package eu.cityopt.sim.eval;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * An expression of unspecific type.
 *
 * @author Hannu Rummukainen
 */
public class Expression {
    protected final String source;
    protected final String name;
    private final Evaluator evaluator;
    private final CompiledScript script;

    Expression(String source, Evaluator evaluator) throws ScriptException {
        this(source, null, evaluator);
    }

    Expression(String source, String name, Evaluator evaluator) throws ScriptException {
        this.source = source;
        this.name = name;
        this.evaluator = evaluator;
        try {
            this.script = evaluator.getCompiler().compile(source);
        } catch (ScriptException e) {
            throw wrapScriptException(e);
        }
    }

    /** Returns the source code of the expression. */
    public String getSource() {
        return source;
    }

    /**
     * Evaluates the expression in the given context.
     */
    public Object evaluate(EvaluationContext context) throws ScriptException {
        try {
            return evaluator.eval(script, context.toBindings(), context.getEvaluationSetup());
        } catch (ScriptException e) {
            throw wrapScriptException(e);
        }
    }

    /**
     * Evaluates the expression and converts the result to the given Type.
     * @return a value object of a compatible value type.
     * @throws ScriptException if evaluation fails
     * @throws InvalidValueException if the result cannot be converted
     */
    public Object evaluateAs(Type type, EvaluationContext context) throws ScriptException {
        Object result = evaluate(context);
        try {
            return type.fromScriptResult(result, context.getEvaluationSetup());
        } catch (InvalidValueException e) {
            throw new InvalidValueException(mungeErrorMessage(e.getMessage()));
        } catch (ScriptException e) {
            throw wrapScriptException(e);
        }
    }

    /**
     * Evaluates the expression and converts the result to a double.
     * @throws ScriptException if evaluation fails
     * @throws InvalidValueException if the result cannot be converted
     */
    public double evaluateDouble(EvaluationContext context) throws ScriptException {
        return (Double) evaluateAs(Type.DOUBLE, context);
    }

    private ScriptException wrapScriptException(ScriptException e) {
        return new ScriptException(mungeErrorMessage(e.getMessage()));
    }

    String mungeErrorMessage(String error) {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append("In ").append(kind()).append(' ').append(name).append(": ");
        }
        // Remove non-informative filename.
        error = error.replaceFirst(" in <script>", "");
        // Drop line number from the message if the expression is a one-liner.
        if ( ! source.contains("\n")) {
            error = error.replaceFirst(" at line number 1", "");
        }
        sb.append(error).append("; source: ").append(source);
        return sb.toString();
    }

    protected String kind() {
        return "expression";
    }
}
