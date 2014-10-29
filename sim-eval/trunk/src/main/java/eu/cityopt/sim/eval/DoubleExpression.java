package eu.cityopt.sim.eval;

import javax.script.CompiledScript;
import javax.script.ScriptException;

public class DoubleExpression {
	private String source;
	private CompiledScript script;

	DoubleExpression(String source, Evaluator evaluator) throws ScriptException {
		this.source = source;
		this.script = evaluator.getCompiler().compile(source);
	}

	public double evaluate(EvaluationContext context)
			throws ScriptException, InvalidValueException {
		Object o = script.eval(context.toBindings());
		double value;
		if (o instanceof Integer) {
			value = (Integer) o;
		} else if (o instanceof Double) {
			value = (Double) o;
		} else if (o instanceof Boolean) {
			value = (Double) o;
		} else {
			throw new InvalidValueException(o, source);
		}
		return value;
	}
}
