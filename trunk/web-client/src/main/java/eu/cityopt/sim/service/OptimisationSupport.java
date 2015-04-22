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
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.repository.OptSearchConstRepository;
import eu.cityopt.repository.ScenGenObjectiveFunctionRepository;
import eu.cityopt.repository.ScenGenOptConstraintRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationProblem;

/**
 * Support functions for database optimisation and scenario generation
 * optimisation.
 *
 * @author Hannu Rummukainen
 */
@Service
public class OptimisationSupport {
    private static Logger log = Logger.getLogger(OptimisationSupport.class); 

    private @Autowired TypeRepository typeRepository;
    private @Autowired ScenGenOptConstraintRepository scenGenOptConstraintRepository;
    private @Autowired OptSearchConstRepository optSearchConstRepository;
    private @Autowired OptConstraintRepository optConstraintRepository;
    private @Autowired ScenGenObjectiveFunctionRepository scenGenObjectiveFunctionRepository;
    private @Autowired ObjectiveFunctionRepository objectiveFunctionRepository;

    private @Autowired SimulationService simulationService;
    private @Autowired SyntaxCheckerService syntaxCheckerService;


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

    /**
     * Evaluates metric, constraints and objective functions on all scenarios
     * of a project.
     * @param project the project in which to find the scenarios
     * @param optimizationSet specifies the constraints and objective function
     * @return structure containing the objective values of the scenarios that
     *   are feasible with respect to the constraints, and information on any
     *   problems encountered.
     * @throws ParseException
     * @throws ScriptException
     */
    public EvaluationResults evaluateScenarios(
            Project project, OptimizationSet optimizationSet)
            throws ParseException, ScriptException {
        OptimisationProblem problem = loadOptimisationProblem(project, optimizationSet);
        syntaxCheckerService.checkOptimizationSet(problem);
        ExternalParameters externals = problem.getExternalParameters();
        ObjectiveExpression objective = problem.objectives.get(0);

        SimulationStorage storage =
                simulationService.makeDbSimulationStorage(project.getPrjid(), externals);

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
                        MetricValues metricValues = new MetricValues(results, problem.metrics);
                        storage.updateMetricValues(metricValues);

                        ConstraintStatus constraintValues =
                                new ConstraintStatus(metricValues, problem.constraints);
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

    /**
     * Fills a sim-eval OptimisationProblem structure with a database search
     * problem definition from an OptimizationSet.
     * The inputConst, decisionVars and inputExprs fields are left empty. 
     */
    public OptimisationProblem loadOptimisationProblem(
            Project project, OptimizationSet optimizationSet)
                    throws ParseException, ScriptException {
        Namespace namespace = simulationService.makeProjectNamespace(project);
        ExternalParameters externals = simulationService.loadExternalParametersFromSet(
                optimizationSet.getExtparamvalset(), namespace);

        OptimisationProblem problem = new OptimisationProblem(null, externals);
        problem.metrics = simulationService.loadMetricExpressions(project, namespace);
        problem.constraints = loadConstraints(optimizationSet, namespace);
        problem.objectives.add(loadObjective(
                optimizationSet.getObjectivefunction(), namespace));
        return problem;
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
        return new Constraint(optConstraint.getOptconstid(), optConstraint.getName(),
                optConstraint.getExpression(), lb, ub, setup.evaluator);
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
                objectiveFunction.getObtfunctionid(), objectiveFunction.getName(),
                objectiveFunction.getExpression(),
                objectiveFunction.getIsmaximise(), namespace.evaluator);
    }

    public void saveConstraints(
            ScenarioGenerator scenarioGenerator,
            List<Constraint> constraints, EvaluationSetup setup) {
        for (Constraint constraint : constraints) {
            OptConstraint optConstraint = saveConstraint(
                    scenarioGenerator.getProject(), constraint, setup);

            ScenGenOptConstraint scenGenOptConstraint = new ScenGenOptConstraint();
            optConstraint.getScengenoptconstraints().add(scenGenOptConstraint);
            scenGenOptConstraint.setOptconstraint(optConstraint);

            scenGenOptConstraint.setScenariogenerator(scenarioGenerator);
            scenarioGenerator.getScengenoptconstraints().add(scenGenOptConstraint);
        }
        scenGenOptConstraintRepository.save(scenarioGenerator.getScengenoptconstraints());
    }

    public void saveConstraints(
            Project project, OptimizationSet optimizationSet, List<Constraint> constraints,
            EvaluationSetup setup) {
        for (Constraint constraint : constraints) {
            OptConstraint optConstraint = saveConstraint(project, constraint, setup);

            OptSearchConst optSearchConst = new OptSearchConst();
            optConstraint.getOptsearchconsts().add(optSearchConst);
            optSearchConst.setOptconstraint(optConstraint);

            optSearchConst.setOptimizationset(optimizationSet);
            optimizationSet.getOptsearchconsts().add(optSearchConst);
        }
        optSearchConstRepository.save(optimizationSet.getOptsearchconsts());
    }

    private OptConstraint saveConstraint(
            Project project, Constraint constraint, EvaluationSetup setup) {
        OptConstraint optConstraint = new OptConstraint();
        optConstraint.setName(constraint.getName());
        optConstraint.setExpression(constraint.getExpression().getSource());
        optConstraint.setLowerbound(Type.DOUBLE.format(constraint.getLowerBound(), setup));
        optConstraint.setUpperbound(Type.DOUBLE.format(constraint.getUpperBound(), setup));
        optConstraint.setProject(project);
        project.getOptconstraints().add(optConstraint);
        return optConstraintRepository.save(optConstraint);
    }

    public void saveObjectives(ScenarioGenerator scenarioGenerator,
            List<ObjectiveExpression> objectives) {
        for (ObjectiveExpression objective : objectives) {
            ObjectiveFunction objectiveFunction = saveObjective(
                    scenarioGenerator.getProject(), objective);

            ScenGenObjectiveFunction scenGenObjectiveFunction = new ScenGenObjectiveFunction();
            objectiveFunction.getScengenobjectivefunctions().add(scenGenObjectiveFunction);
            scenGenObjectiveFunction.setObjectivefunction(objectiveFunction);

            scenGenObjectiveFunction.setScenariogenerator(scenarioGenerator);
            scenarioGenerator.getScengenobjectivefunctions().add(scenGenObjectiveFunction);
        }
        scenGenObjectiveFunctionRepository.save(scenarioGenerator.getScengenobjectivefunctions());
    }

    public void saveObjective(Project project, OptimizationSet optimizationSet,
            ObjectiveExpression objective) {
        ObjectiveFunction objectiveFunction = saveObjective(project, objective);

        objectiveFunction.getOptimizationsets().add(optimizationSet);
        optimizationSet.setObjectivefunction(objectiveFunction);
    }

    private ObjectiveFunction saveObjective(Project project, ObjectiveExpression objective) {
        ObjectiveFunction objectiveFunction = new ObjectiveFunction();
        objectiveFunction.setExpression(objective.getSource());
        objectiveFunction.setIsmaximise(objective.isMaximize());
        objectiveFunction.setName(objective.getName());
        eu.cityopt.model.Type type = typeRepository.findByNameLike(Type.DOUBLE.name);
        objectiveFunction.setType(type);

        objectiveFunction.setProject(project);
        project.getObjectivefunctions().add(objectiveFunction);
        return objectiveFunctionRepository.save(objectiveFunction);
    }
}
