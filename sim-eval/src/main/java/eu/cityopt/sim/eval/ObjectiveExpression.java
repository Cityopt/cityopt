package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * Represents an objective in an optimization problem.
 * Can be used for both single-objective and multi-objective problems.
 *
 * @author Hannu Rummukainen
 */
public class ObjectiveExpression extends Expression {
    private final boolean maximize;

    public ObjectiveExpression(String objectiveName, String source,
            boolean maximize, Evaluator evaluator) throws ScriptException {
        super(source, objectiveName, evaluator);
        this.maximize = maximize;
    }

    /** Returns a symbolic name for the objective function. */
    public String getName() {
        return name;
    }

    public boolean isMaximize() {
        return maximize;
    }

    public double evaluateAsMinGoal(EvaluationContext context)
            throws ScriptException {
        return flipSignIfMax(evaluateDouble(context));
    }

    public double flipSignIfMax(double value) {
        return maximize ? -value : value;
    }

    @Override
    protected String kind() {
        return "objective";
    }
}
