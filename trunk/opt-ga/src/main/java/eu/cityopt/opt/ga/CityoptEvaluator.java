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
