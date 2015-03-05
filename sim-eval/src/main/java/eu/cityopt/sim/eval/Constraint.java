package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * Represents a constraint in an optimization problem.
 *
 * @author Hannu Rummukainen
 */
public class Constraint {
    private String name;
    private Expression expression;
    private double lowerBound;
    private double upperBound;

    /**
     * Construct a new constraint.
     * @throws IllegalArgumentException if lowerBound > upperBound  
     */
    public Constraint(String constraintName, String source,
            double lowerBound, double upperBound, Evaluator evaluator)
            throws ScriptException {
        this.name = constraintName;
        this.expression = new Expression(source, evaluator);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("Lower bound " + lowerBound
                    + " exceeds upper bound " + upperBound + " of constraint "
                    + source);
        }
    }

    /** Returns a symbolic name for the constraint. */
    public String getName() {
        return name;
    }

    /**
     * Computes the infeasibility of the constraint, i.e. by how much it is
     * violated. 0.0 means that the constraint is satisfied, positive values
     * that it is unsatisfied.
     * @throws ScriptException 
     */
    public double infeasibility(EvaluationContext context) throws ScriptException {
        Object value = expression.evaluate(context);
        if (value instanceof Number) {
            double d = ((Number) value).doubleValue();
            if (d < lowerBound) {
                return lowerBound - d;
            } else if (d > upperBound) {
                return d - upperBound;
            } else {
                return 0.0;
            }
        } else if (value instanceof TimeSeriesI) {
           TimeSeriesI ts = (TimeSeriesI) value;
           double min = ts.getMin();
           double max = ts.getMax();
           if (min < lowerBound) {
               return lowerBound - min;
           } else if (max > upperBound) {
               return max - upperBound;
           } else {
               return 0.0;
           }
        } else {
            throw new InvalidValueException(value, expression.source);
        }
    }
}
