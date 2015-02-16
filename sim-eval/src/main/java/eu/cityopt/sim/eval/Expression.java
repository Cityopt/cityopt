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
     * Coerces the result to one of the types specified in Type.
     */
    public Object evaluate(EvaluationContext context) throws ScriptException {
        Object o = evaluator.eval(script, context.toBindings(), context.getEvaluationSetup());
        return Type.normalize(o);
    }

    /**
     * Evaluates the expression and converts the result to a double.
     * @throws InvalidValueException if the result is of an incompatible type
     */
    public double evaluateDouble(EvaluationContext context) throws ScriptException,
            InvalidValueException {
        Object o = evaluator.eval(script, context.toBindings(), context.getEvaluationSetup());
        double value;
        if (o instanceof Number) {
            value = ((Number) o).doubleValue();
        } else {
            throw new InvalidValueException(o, source);
        }
        return value;
    }
}
