package eu.cityopt.sim.eval;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * An expression evaluating to a double precision floating point value.
 *
 * @author Hannu Rummukainen
 */
public class DoubleExpression {
    private final String source;
    private final Evaluator evaluator;
    private final CompiledScript script;

    DoubleExpression(String source, Evaluator evaluator) throws ScriptException {
        this.source = source;
        this.evaluator = evaluator;
        this.script = evaluator.getCompiler().compile(source);
    }

    public double evaluate(EvaluationContext context) throws ScriptException,
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
