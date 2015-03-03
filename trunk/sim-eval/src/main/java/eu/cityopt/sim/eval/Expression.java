package eu.cityopt.sim.eval;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * An expression of unspecific type.
 *
 * @author Hannu Rummukainen
 */
public class Expression {
    final String source;
    private final Evaluator evaluator;
    private final CompiledScript script;

    Expression(String source, Evaluator evaluator) throws ScriptException {
        this.source = source;
        this.evaluator = evaluator;
        this.script = evaluator.getCompiler().compile(source);
    }

    /**
     * Evaluates the expression in the given context.
     */
    public Object evaluate(EvaluationContext context) throws ScriptException {
        return evaluator.eval(script, context.toBindings(), context.getEvaluationSetup());
    }

    /**
     * Evaluates the expression and converts the result to the given Type.
     * @return a value object of a compatible value type.
     * @throws ScriptException if evaluation fails
     * @throws InvalidValueException if the result cannot be converted
     */
    public Object evaluateAs(Type type, EvaluationContext context) throws ScriptException {
        Object result = evaluator.eval(script, context.toBindings(), context.getEvaluationSetup());
        try {
            return type.fromScriptResult(result, context.getEvaluationSetup());
        } catch (InvalidValueException e) {
            throw new InvalidValueException(type, result, source);
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
}
