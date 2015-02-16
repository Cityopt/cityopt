package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * Represents an objective in an optimization problem.
 * Can be used for both single-objective and multi-objective problems.
 *
 * @author Hannu Rummukainen
 */
public class ObjectiveExpression extends Expression {
    private int objectiveId;
    private boolean maximize;

    public ObjectiveExpression(int objectiveId, String source,
            boolean maximize, Evaluator evaluator) throws ScriptException {
        super(source, evaluator);
        this.objectiveId = objectiveId;
        this.maximize = maximize;
    }

    public int getObjectiveId() {
        return objectiveId;
    }

    public boolean isMaximize() {
        return maximize;
    }

    public double evaluateAsMinGoal(EvaluationContext context)
            throws ScriptException, InvalidValueException {
        return flipSignIfMax(evaluateDouble(context));
    }

    public double flipSignIfMax(double value) {
        return maximize ? -value : value;
    }
}
