package eu.cityopt.sim.service;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptSearchConst;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenGenObjectiveFunction;
import eu.cityopt.model.ScenGenOptConstraint;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.repository.OptSearchConstRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenGenObjectiveFunctionRepository;
import eu.cityopt.repository.ScenGenOptConstraintRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.SearchOptimizationResults;
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
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;

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
    private @Autowired OptimizationSetRepository optimizationSetRepository;
    private @Autowired TimeSeriesValRepository timeSeriesValRepository;
    private @Autowired ExtParamValSetRepository extParamValSetRepository;

    private @Autowired SimulationService simulationService;
    private @Autowired SyntaxCheckerService syntaxCheckerService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager em;

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
        @Override
        public String toString() {
            return feasible.size() + " feasible scenarios, "
                    + infeasible.size() + " infeasible scenarios, "
                    + ignored.size() + " scenarios had no results, "
                    + failures.size() + " scenarios failed.";
        }
    }

    /**
     * Perform database search optimization
     * @param prjId Project id
     * @param optId Optimization set id
     * @param size Number of best results to return
     * @throws EntityNotFoundException
     * @throws ParseException
     * @throws ScriptException
     */
    @Transactional(readOnly=true)
    public SearchOptimizationResults searchOptimization(
            int prjId, int optId, int size)
            throws EntityNotFoundException, ParseException, ScriptException {
        EvaluationResults er = evaluateScenarios(prjId, optId);
        SearchOptimizationResults sor = new SearchOptimizationResults();
        sor.setEvaluationResult(er);
        sor.resultScenarios = new ArrayList<ScenarioWithObjFuncValueDTO>();

        if (!er.feasible.isEmpty()) {
            //sort evaluation results by value
            Map<Integer, ObjectiveStatus>
            sortedMap = sortByValue(er.feasible);
            //sortedMap.forEach((Integer i, ObjectiveStatus s) -> System.out.println(s.objectiveValues[0]));

            for (Map.Entry<Integer, ObjectiveStatus>
            ent : sortedMap.entrySet()){
                Double value = ent.getValue().objectiveValues[0];

                //add to result list, as long as desired size is not reached
                if (sor.resultScenarios.size() >= size) {
                    break;
                }
                ScenarioWithObjFuncValueDTO
                scenWV = modelMapper.map(
                        em.getReference(Scenario.class,
                                ent.getKey()),
                        ScenarioWithObjFuncValueDTO.class);
                scenWV.setValue(value);
                sor.resultScenarios.add(scenWV);
            }
        }
        return sor;
    }

    /**
     * sorts objectiveStatus map by value, starting with the optimal scenario
     * @param map
     * @return
     */
    private <K,T> Map<K, ObjectiveStatus> sortByValue( Map<K, ObjectiveStatus> map )
    {
         Map<K,ObjectiveStatus> result = new LinkedHashMap<>();
         Stream <Entry<K,ObjectiveStatus>> st = map.entrySet().stream();

         st.sorted(new Comparator<Entry<K,ObjectiveStatus>>(){
                            @Override
                            public int compare(Entry<K, ObjectiveStatus> arg0,
                                            Entry<K, ObjectiveStatus> arg1) {
                                    return arg0.getValue().compareTo(arg1.getValue());
                            }
                    }).forEach(e ->result.put(e.getKey(),e.getValue()));

         return result;
    }

    /**
     * Evaluates metric, constraints and objective functions on all scenarios
     * of a project.
     * @param prjId Project id
     * @param optId OptimizationSet id
     * @return structure containing the objective values of the scenarios that
     *   are feasible with respect to the constraints, and information on any
     *   problems encountered.
     */
    @Transactional(readOnly=true)
    public EvaluationResults evaluateScenarios(int prjId, int optId)
            throws EntityNotFoundException, ParseException, ScriptException {

        Project project = projectRepository.findOne(prjId);
        OptimizationSet optimizationSet = optimizationSetRepository.findOne(
                optId);

        if(project == null)
            throw new EntityNotFoundException("could not find prjId: "+ prjId);
        if(optimizationSet == null)
            throw new EntityNotFoundException("could not find optId: "+ optId);

        if(optimizationSet.getProject().getPrjid() != prjId)
            throw new InvalidParameterException(
                    "optimization set is not part of the project"
                    + optimizationSet);

        return evaluateScenarios(project, optimizationSet);
    }

    /**
     * Evaluates metric, constraints and objective functions on all scenarios
     * of a project.
     * @param project the project in which to find the scenarios
     * @param optimizationSet specifies the constraints and objective function
     * @throws ParseException
     * @throws ScriptException
     */
    private EvaluationResults evaluateScenarios(
            Project project, OptimizationSet optimizationSet)
            throws ParseException, ScriptException {
        OptimisationProblem problem = loadOptimisationProblem(project, optimizationSet);
        syntaxCheckerService.checkOptimizationSet(problem);
        ExternalParameters externals = problem.getExternalParameters();
        ObjectiveExpression objective = problem.objectives.get(0);

        EvaluationResults evaluationResults = new EvaluationResults();
        for (Scenario scenario : project.getScenarios()) {
            try {
                SimulationInput input =
                        simulationService.loadSimulationInput(scenario, externals);
                if (input.isComplete()) {
                    SimulationOutput output =
                            simulationService.loadSimulationOutput(scenario, input);
                    if (output instanceof SimulationResults) {
                        MetricValues
                            metricValues = simulationService.getMetricValues(
                                    scenario,
                                    optimizationSet.getExtparamvalset(),
                                    problem.metrics,
                                    (SimulationResults)output);
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

    public int saveOptimisationSet(Project project, Integer userId, String name,
            OptimisationProblem problem) {
        OptimizationSet optimizationSet = new OptimizationSet();
        optimizationSet.setProject(project);
        optimizationSet.setCreatedby(userId);
        optimizationSet.setCreatedon(new Date());
        optimizationSet.setName(name);

        List<Runnable> idUpdateList = new ArrayList<>();
        ExtParamValSet extParamValSet = simulationService.saveExternalParameterValues(
                project, problem.getExternalParameters(), name, idUpdateList);
        optimizationSet.setExtparamvalset(extParamValSet);

        optimizationSet = optimizationSetRepository.save(optimizationSet);

        saveConstraints(project, optimizationSet, problem.constraints, problem.getNamespace());

        if (problem.objectives.size() >= 1) {
            saveObjective(project, optimizationSet, problem.objectives.get(0));
        }

        optimizationSetRepository.flush();
        timeSeriesValRepository.flush();
        extParamValSetRepository.flush();
        for (Runnable update : idUpdateList) {
            update.run();
        }
        return optimizationSet.getOptid();
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
        OptConstraint optConstraint =
                optConstraintRepository.findByNameAndProject_prjid(
                        constraint.getName(), project.getPrjid());
        if (optConstraint == null) {
            optConstraint = new OptConstraint();
        } // XXX should avoid overwriting by e.g. using a different name
        optConstraint.setName(constraint.getName());
        optConstraint.setExpression(constraint.getExpression().getSource());
        optConstraint.setLowerbound(
                (constraint.getLowerBound() == Double.NEGATIVE_INFINITY)
                ? null : Type.DOUBLE.format(constraint.getLowerBound(), setup));
        optConstraint.setUpperbound(
                (constraint.getUpperBound() == Double.POSITIVE_INFINITY)
                ? null : Type.DOUBLE.format(constraint.getUpperBound(), setup));
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

        objectiveFunctionRepository.save(objectiveFunction);
    }

    private ObjectiveFunction saveObjective(Project project, ObjectiveExpression objective) {
        ObjectiveFunction objectiveFunction =
                objectiveFunctionRepository.findByName(
                        project.getPrjid(), objective.getName());
        if (objectiveFunction == null) {
            objectiveFunction = new ObjectiveFunction();
        } // XXX should avoid overwriting by e.g. using a different name
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
