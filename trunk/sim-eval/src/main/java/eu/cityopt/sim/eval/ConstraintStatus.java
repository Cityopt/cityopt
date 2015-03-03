package eu.cityopt.sim.eval;

import java.util.Collection;

import javax.script.ScriptException;

/**
 * Container for constraint evaluation results from a single simulation run.
 * Constraints can be evaluated either before simulation or after simulation.
 * If a constraint refers to simulation results, evaluating it before simulation
 * will fail, but it is possible to ignore such errors.
 *
 * @author Hannu Rummukainen
 */
public class ConstraintStatus implements PartiallyComparable<ConstraintStatus> {
    /**
     * Whether all constraints are feasible. This is null if SimulationInput was
     * evaluated, and some constraints depend on values that are not yet
     * available.
     */
    public final Boolean feasible;

    /**
     * Infeasibility values of individual constraints, in the same order as the
     * constructor arguments. Values are set NaN if constraint evaluation fails
     * and errors are ignored.
     */
    public final double[] infeasibilities;

    /**
     * Evaluates pre-simulation constraint status with external parameters
     * and input parameters defined.
     */
    public ConstraintStatus(SimulationInput input,
            Collection<Constraint> constraints, boolean ignoreErrors)
            throws ScriptException {
        this.infeasibilities = computeInfeasibilities(input, constraints,
                ignoreErrors);
        this.feasible = computeFeasibility(infeasibilities);
    }

    /**
     * Evaluates post-simulation constraint status with external parameters,
     * input parameters, output variables and metrics defined.
     */
    public ConstraintStatus(MetricValues values,
            Collection<Constraint> constraints) throws ScriptException {
        this.infeasibilities = computeInfeasibilities(values, constraints,
                false);
        this.feasible = computeFeasibility(infeasibilities);
        if (feasible == null) {
            throw new IllegalStateException();
        }
    }

    /**
     * Evaluates pre- or post-simulation constraint status with decision
     * values defined, in addition to other named values.
     */
    public ConstraintStatus(ConstraintContext values,
            Collection<Constraint> constraints, boolean ignoreErrors)
            throws ScriptException {
        this.infeasibilities = computeInfeasibilities(values, constraints, ignoreErrors);
        this.feasible = computeFeasibility(infeasibilities);
    }

    public boolean mayBeFeasible() {
        return feasible == null || feasible == true;
    }

    @Override
    public Integer compareTo(ConstraintStatus other) {
        return PartialComparisons.compare(infeasibilities,
                other.infeasibilities);
    }

    public static Integer compare(ConstraintStatus a, ConstraintStatus b) {
        return a.compareTo(b);
    }

    static private double[] computeInfeasibilities(EvaluationContext context,
            Collection<Constraint> constraints, boolean ignoreErrors)
                    throws ScriptException {
        double[] infeasibilities = new double[constraints.size()];
        int i = 0;
        for (Constraint constraint : constraints) {
            try {
                infeasibilities[i] = constraint.infeasibility(context);
            } catch (ScriptException e) {
                if (ignoreErrors) {
                    infeasibilities[i] = Double.NaN;
                } else {
                    throw e;
                }
            }
            ++i;
        }
        return infeasibilities;
    }

    static private Boolean computeFeasibility(double[] infeasibilities) {
        for (double d : infeasibilities) {
            if (Double.isNaN(d)) {
                return null;
            } else if (d > 0) {
                return false;
            }
        }
        return true;
    }
}
