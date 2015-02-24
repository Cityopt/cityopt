package eu.cityopt.opt.ga;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.script.ScriptException;

import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;

import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.InvalidValueException;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;

/**
 * The Cityopt evaluator for Opt4J.
 * Constraints are handled as follows:
 * <ul>
 * <li>All constraint infeasibilities are added to the returned
 * {@link Objectives}.
 * <li>A priori constraints are those that can be evaluated before simulation.
 * The rest are a posteriori constraints.
 * <li>If any a priori constraint is infeasible, no simulation is performed.
 * The a priori infeasibilities are returned and the rest of Objectives
 * (a posteriori infeasibilities and actual objectives) is set to null.
 * <li>If any a posteriori constraint is infeasible, all infeasibilities
 * are returned but the actual objectives are set to null.
 * <li>Otherwise all infeasibilities (all zero) and actual objectives
 * are returned normally.
 * </ul>
 * Opt4J interprets null values as infinitely bad, so hopefully this
 * implements constraint domination in Opt4J genetic algorithms.
 * A more straightforward approach would be to extend Objectives to
 * include constraints and override {@link Objectives#dominates(Objectives)},
 * but that appears unsupported by Opt4J. 
 * @author ttekth
 *
 */
public class CityoptEvaluator implements Evaluator<SimulationInput> {
    private OptimisationProblem problem;
    
    @Inject
    public CityoptEvaluator(OptimisationProblem problem) {
        this.problem = problem;
    }

    @Override
    public Objectives evaluate(SimulationInput phenotype) {
        try {
            ConstraintStatus prior = new ConstraintStatus(
                    phenotype, problem.constraints, true);
            if (!prior.mayBeFeasible()) {
                return infeasibleObj(prior); 
            }
            SimulationOutput out = problem.runner.start(phenotype).get();
            if (!(out instanceof SimulationResults)) {
                throw new RuntimeException("Simulation failure");
            }
            MetricValues mv = new MetricValues(
                    (SimulationResults)out, problem.metrics);
            ConstraintStatus post = new ConstraintStatus(
                    mv, problem.constraints);
            if (!post.mayBeFeasible()) {
                return infeasibleObj(post);
            }
            ObjectiveStatus ost = new ObjectiveStatus(mv, problem.objs);
            Objectives obj = toObjectives(post);
            for (int i = 0; i != problem.objs.size(); ++i) {
                ObjectiveExpression o = problem.objs.get(i);
                obj.add(o.getName(), o.isMaximize() ? Sign.MAX : Sign.MIN,
                        ost.objectiveValues[i]);
            }
            return obj;
        } catch (ScriptException | InvalidValueException e) {
            throw new RuntimeException("Evaluation error", e);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException("Execution error", e);
        }
    }
    
    private Objectives toObjectives(ConstraintStatus st) {
        Objectives obj = new Objectives();
        for (int i = 0; i != problem.constraints.size(); ++i) {
            String name = problem.constraints.get(i).getName();
            double infeas = st.infeasibilities[i]; 
            obj.add(name, Sign.MIN, Double.isNaN(infeas) ? null : infeas);
        }
        return obj;
    }

    private Objectives infeasibleObj(ConstraintStatus st) {
        Objectives obj = toObjectives(st);
        for (ObjectiveExpression o : problem.objs) {
            obj.add(o.getName(), o.isMaximize() ? Sign.MAX : Sign.MIN, null);
        }
        return obj;
    }
}
