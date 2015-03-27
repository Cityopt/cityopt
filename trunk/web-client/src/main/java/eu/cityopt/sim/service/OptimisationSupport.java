package eu.cityopt.sim.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptSearchConst;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenGenObjectiveFunction;
import eu.cityopt.model.ScenGenOptConstraint;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationStorage;

/**
 * Support functions for database optimisation and scenario generation
 * optimisation.
 *
 * @author Hannu Rummukainen
 */
@Service
public class OptimisationSupport {
    private static Logger log = Logger.getLogger(OptimisationSupport.class); 

    private @Autowired ApplicationContext applicationContext;
    private @Autowired SimulationService simulationService;


    /** Results from {@link OptimisationSupport#evaluateScenarios(Project, OptimizationSet)} */
    public static class EvaluationResults {
        /**
         * The objective values for feasible scenarios.
         * Map from Scenario id to objective value.
         */
        public Map<Integer, ObjectiveStatus> feasible = new HashMap<>();

        /** The infeasible scenarios.  Set of Scenario ids. */
        public Set<Integer> infeasible = new HashSet<Integer>();

        /**
         * Scenarios with no or incomplete results (or input), and thus
         * no metric values.  Set of Scenario ids.
         */
        public Set<Integer> ignored = new HashSet<Integer>();

        /**
         * Evaluation failures.
         * Map from Scenario id to Exception encountered in evaluation.
         */
        public Map<Integer, Exception> failures = new HashMap<Integer, Exception>();

        /** Brief human-readable description. */
        public String toString() {
            return feasible.size() + " feasible scenarios, "
                    + infeasible.size() + " infeasible scenarios, "
                    + ignored.size() + " scenarios had no results, "
                    + failures.size() + " scenarios failed.";
        }
    }

    public EvaluationResults evaluateScenarios(
            Project project, OptimizationSet optimizationSet)
            throws ParseException, ScriptException {
        Namespace namespace = simulationService.makeProjectNamespace(project);
        ExternalParameters externals = simulationService.loadExternalParametersFromSet(
                optimizationSet.getExtparamvalset(), namespace);

        SimulationStorage storage =
                simulationService.makeDbSimulationStorage(project.getPrjid(), externals);

        List<MetricExpression> metricExpressions =
                simulationService.loadMetricExpressions(project, namespace);

        List<Constraint> constraints = loadConstraints(optimizationSet, namespace);
        ObjectiveExpression objective = loadObjective(
                optimizationSet.getObjectivefunction(), namespace);

        EvaluationResults evaluationResults = new EvaluationResults();
        for (Scenario scenario : project.getScenarios()) {
            try {
                SimulationInput input =
                        simulationService.loadSimulationInput(scenario, externals);
                if (input.isComplete()) {
                    SimulationOutput output =
                            simulationService.loadSimulationOutput(scenario, input);
                    if (output instanceof SimulationResults) {
                        SimulationResults results = (SimulationResults) output;
                        MetricValues metricValues = new MetricValues(results, metricExpressions);
                        storage.updateMetricValues(metricValues);

                        ConstraintStatus constraintValues =
                                new ConstraintStatus(metricValues, constraints);
                        if (constraintValues.feasible) {
                            ObjectiveStatus objectiveStatus =
                                    new ObjectiveStatus(metricValues, objective);
                            evaluationResults.feasible.put(scenario.getScenid(), objectiveStatus);
                        } else {
                            evaluationResults.infeasible.add(scenario.getScenid());
                        }
                    } else {
                        evaluationResults.ignored.add(scenario.getScenid());
                    }
                } else {
                    evaluationResults.ignored.add(scenario.getScenid());
                }
            } catch (ParseException | ScriptException e) {
                log.warn("Failed to evaluate scenario " + scenario.getScenid()
                        + ": " + e.getMessage());
                evaluationResults.failures.put(scenario.getScenid(), e);
            }
        }
        log.info("Evaluated scenarios of project " + project.getPrjid() + ": "
                + evaluationResults);
        return evaluationResults;
    }

    public List<Constraint> loadConstraints(
            ScenarioGenerator scenarioGenerator, Namespace namespace)
                    throws ScriptException {
        List<Constraint> simConstraints = new ArrayList<Constraint>();
        for (ScenGenOptConstraint scenGenOptConstraint 
                : scenarioGenerator.getScengenoptconstraints()) {
            OptConstraint optConstraint = scenGenOptConstraint.getOptconstraint();
            simConstraints.add(loadConstraint(optConstraint, namespace));
        }
        return simConstraints;
    }

    public List<Constraint> loadConstraints(
            OptimizationSet optimizationSet, Namespace namespace)
                    throws ScriptException {
        List<Constraint> simConstraints = new ArrayList<>();
        for (OptSearchConst optSearchConst : optimizationSet.getOptsearchconsts()) {
            OptConstraint optConstraint = optSearchConst.getOptconstraint();
            simConstraints.add(loadConstraint(optConstraint, namespace));
        }
        return simConstraints;
    }

    private Constraint loadConstraint(
            OptConstraint optConstraint, EvaluationSetup setup)
                    throws ScriptException {
        String lbText = optConstraint.getLowerbound();
        String ubText = optConstraint.getUpperbound();
        double lb = (lbText != null) ? Double.valueOf(lbText) : Double.NEGATIVE_INFINITY;
        double ub = (ubText != null) ? Double.valueOf(ubText) : Double.POSITIVE_INFINITY;
        return new Constraint(optConstraint.getName(), optConstraint.getExpression(),
                lb, ub, setup.evaluator);
    }

    public List<ObjectiveExpression> loadObjectives(
            ScenarioGenerator scenarioGenerator, Namespace namespace)
                    throws ScriptException {
        List<ObjectiveExpression> simObjectives = new ArrayList<>();
        for (ScenGenObjectiveFunction scenGenObjectiveFunction : scenarioGenerator.getScengenobjectivefunctions()) {
            ObjectiveFunction objectiveFunction = scenGenObjectiveFunction.getObjectivefunction();
            simObjectives.add(loadObjective(objectiveFunction, namespace));
        }
        return simObjectives;
    }

    public ObjectiveExpression loadObjective(
            ObjectiveFunction objectiveFunction, Namespace namespace)
                    throws ScriptException
    {
        //TODO maybe save objectiveFunction.getType() for formatting values?
        return new ObjectiveExpression(
                objectiveFunction.getName(), objectiveFunction.getExpression(),
                objectiveFunction.getIsmaximise(), namespace.evaluator);
    }
}
