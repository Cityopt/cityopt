package eu.cityopt.opt.ga;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Singleton;
import javax.script.ScriptException;

import org.opt4j.core.Objective.Sign;
import org.opt4j.core.Objectives;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerStateListener;
import org.opt4j.core.problem.Evaluator;

import com.google.inject.Inject;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.ConstraintContext;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.opt.OptimisationLog;
import eu.cityopt.sim.opt.OptimisationProblem;

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
@Singleton
public class CityoptEvaluator
implements Evaluator<CityoptPhenotype>, OptimizerStateListener, Closeable {
    private final OptimisationProblem problem;
    private volatile SimulationRunner runner;
    private Set<Future<SimulationOutput>> jobs = ConcurrentHashMap.newKeySet();
    private SimulationStorage storage = new SimulationStorage() {        
        @Override
        public void updateMetricValues(MetricValues metricValues) {}
        
        @Override
        public void put(Put put) {}
        
        @Override
        public SimulationOutput get(SimulationInput input) {return null;}

        @Override
        public Iterator<SimulationOutput> iterator() {
            return Collections.emptyIterator();
        }
    };
    private OptimisationLog userLog =
            m -> System.err.println(m);

    @Inject
    public CityoptEvaluator(OptimisationProblem problem)
                    throws IOException, ConfigurationException {
        this.problem = problem;
        runner = problem.makeRunner();
    }
    
    @Inject(optional=true)
    public void setStorage(SimulationStorage storage) {
        this.storage = storage;
    }

    @Inject(optional=true)
    public void setUserLog(OptimisationLog log) {
        this.userLog = log;
    }

    @Override
    public Objectives evaluate(CityoptPhenotype pt) {
        Future<SimulationOutput> job = null;
        SimulationStorage.Put put = new SimulationStorage.Put(pt.input, pt.description);
        put.decisions = pt.decisions;
        try {
            ConstraintContext coco = new ConstraintContext(
                    pt.decisions, pt.input);
            ConstraintStatus prior = new ConstraintStatus(
                    coco, problem.constraints, true);
            put.constraintStatus = prior;
            if (!prior.mayBeFeasible()) {
                return infeasibleObj(prior); 
            }
            if (runner == null) {
                throw new RuntimeException("Closed evaluator called.");
            }
            SimulationOutput out = storage.get(pt.input);
            if (out == null) {
                job = runner.start(pt.input);
                jobs.add(job);
                out = job.get();
                jobs.remove(job);
                job = null;
                put.output = out;
            }
            if (!(out instanceof SimulationResults)) {
                userLog.logSimulationFailure(pt.description, (SimulationFailure) out);
                return infeasibleObj(prior);
            }
            MetricValues mv = new MetricValues(
                    (SimulationResults)out, problem.metrics);
            put.metricValues = mv;
            ConstraintStatus post = new ConstraintStatus(
                    new ConstraintContext(coco, mv),
                    problem.constraints, false);
            put.constraintStatus = post;
            if (!post.isDefinitelyFeasible()) {
                return infeasibleObj(post);
            }
            ObjectiveStatus ost = new ObjectiveStatus(mv, problem.objectives);
            put.objectiveStatus = ost;
            Objectives obj = toObjectives(post);
            for (int i = 0; i != problem.objectives.size(); ++i) {
                ObjectiveExpression o = problem.objectives.get(i);
                obj.add(getOName(o), o.isMaximize() ? Sign.MAX : Sign.MIN,
                        ost.objectiveValues[i]);
            }
            return obj;
        } catch (ScriptException e) {
            userLog.logEvaluationFailure(pt.description, e);
            throw new RuntimeException("Evaluation error", e);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException("Execution error", e);
        } finally {
            if (put.output != null) {
                storage.put(put);
            }
            if (job != null) {
                job.cancel(true);
                jobs.remove(job);
            }
        }
    }
    
    private Objectives toObjectives(ConstraintStatus st) {
        Objectives obj = new Objectives();
        for (int i = 0; i != problem.constraints.size(); ++i) {
            String name = getOName(problem.constraints.get(i));
            double infeas = st.infeasibilities[i];
            if (Double.isNaN(infeas)) {
                obj.add(name, Sign.MIN, null);
            } else {
                obj.add(name, Sign.MIN, infeas);
            }
        }
        return obj;
    }

    private Objectives infeasibleObj(ConstraintStatus st) {
        Objectives obj = toObjectives(st);
        for (ObjectiveExpression o : problem.objectives) {
            obj.add(getOName(o), o.isMaximize() ? Sign.MAX : Sign.MIN, null);
        }
        return obj;
    }

    public static String getOName(Constraint c) {
        return "C" + c.getName();
    }

    public static String getOName(ObjectiveExpression o) {
        return "O" + o.getName();
    }

    @Override
    public void close() throws IOException {
        if (runner != null) {
            SimulationRunner r = runner;
            runner = null;
            cancelJobs();
            r.close();
        }
    }

    private void cancelJobs() {
        while (!jobs.isEmpty()) {
            Iterator<Future<SimulationOutput>> it = jobs.iterator();
            while (it.hasNext()) {
                it.next().cancel(true);
                it.remove();
            }
        }
    }

    @Override
    public void optimizationStarted(Optimizer optimizer) {}

    @Override
    public void optimizationStopped(Optimizer optimizer) {
        try {
            close();
        } catch (IOException e) {}
    }

    public OptimisationProblem getProblem() {
        return problem;
    }
}
