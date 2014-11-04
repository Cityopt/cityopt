package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * Represents a constraint in an optimization problem.
 *
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class ConstraintExpression extends DoubleExpression {
    private int constraintId;
    private double lowerBound;
    private double upperBound;

    public ConstraintExpression(int constraintId, String source,
            double lowerBound, double upperBound, Evaluator evaluator)
            throws ScriptException, EvaluationException {
        super(source, evaluator);
        this.constraintId = constraintId;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        if (lowerBound > upperBound) {
            throw new EvaluationException("Lower bound " + lowerBound
                    + " exceeds upper bound " + upperBound + " of constraint "
                    + source);
        }
    }

    public int getConstraintId() {
        return constraintId;
    }

    /**
     * Computes the infeasibility of the constraint, i.e. by how much it is
     * violated. 0.0 means that the constraint is satisfied, positive values
     * that it is unsatisfied.
     */
    @Override
    public double evaluate(EvaluationContext context) throws ScriptException,
            InvalidValueException {
        double value = super.evaluate(context);
        if (value < lowerBound) {
            return lowerBound - value;
        } else if (value > upperBound) {
            return value - upperBound;
        } else {
            return 0.0;
        }
    }
}
