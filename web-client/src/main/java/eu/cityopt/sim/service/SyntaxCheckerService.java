package eu.cityopt.sim.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.script.ScriptException;

import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.Symbol;
import eu.cityopt.sim.eval.SyntaxChecker;
import eu.cityopt.sim.opt.OptimisationProblem;

/**
 * Checks expressions for errors without running them. Facade for
 * {@link SyntaxChecker}. As noted in {@link SyntaxChecker} documentation, the
 * checking is not complete, i.e. it does not detect all possible errors that
 * could occur when the expressions are used.
 *
 * @author Hannu Rummukainen
 */
@Named
@Singleton
public class SyntaxCheckerService {
    private @Inject SimulationService simulationService;
    private @Inject ScenarioGenerationService scenarioGenerationService;
    private @Inject OptimisationSupport optimisationSupport;

    private @Inject ProjectRepository projectRepository;
    private @Inject ScenarioGeneratorRepository scenarioGeneratorRepository;
    private @Inject OptimizationSetRepository optimizationSetRepository;

    /**
     * Provides a SyntaxChecker instance for checking expressions and
     * identifiers in the given project.
     */
    @Transactional
    public SyntaxChecker getSyntaxChecker(int projectId) {
        Project project = projectRepository.findOne(projectId);
        Namespace namespace = simulationService.makeProjectNamespace(project);
        return new SyntaxChecker(simulationService.getEvaluator(), namespace, true);
    }

    /**
     * Checks the metric expressions of the given project.
     * @throws ScriptException if any errors are found; the details are
     *  in the exception message.
     */
    @Transactional
    public void checkMetricExpressions(int projectId) throws ScriptException {
        Project project = projectRepository.findOne(projectId);
        Namespace namespace = simulationService.makeProjectNamespace(project);
        Collection<MetricExpression> metrics =
                simulationService.loadMetricExpressions(project, namespace);
        checkMetricExpressions(metrics, namespace);
    }

    public void checkMetricExpressions(Collection<MetricExpression> metrics,
            Namespace namespace) throws ScriptException {
        SyntaxChecker syntaxChecker = new SyntaxChecker(
                simulationService.getEvaluator(), namespace, true);
        StringBuilder messages = new StringBuilder();
        for (MetricExpression metric : metrics) {
            SyntaxChecker.Error error = syntaxChecker.checkMetricExpression(metric);
            if (error != null) messages.append(error.message).append('\n');
        }
        if (messages.length() > 0) {
            throw new ScriptException(messages.toString());
        }
    }

    /**
     * Checks if errors will occur with an OptimizationSet. Checks specifically
     * the metric expressions of the project, and the constraints and the
     * objective from the optimization set.
     * @throws ScriptException if any errors are found; the details are
     *  in the exception message.
     */
    @Transactional
    public void checkOptimizationSet(int projectId, int optimizationSetId)
            throws ParseException, ScriptException {
        Project project = projectRepository.findOne(projectId);
        OptimizationSet optimizationSet = optimizationSetRepository.findOne(optimizationSetId);
        OptimisationProblem problem =
                optimisationSupport.loadOptimisationProblem(project, optimizationSet);
        checkOptimizationSet(problem);
    }

    public void checkOptimizationSet(OptimisationProblem problem) throws ScriptException {
        SyntaxChecker syntaxChecker = new SyntaxChecker(
                simulationService.getEvaluator(), problem.getNamespace(), true);
        StringBuilder messages = new StringBuilder();
        checkCommonProblemParts(problem, syntaxChecker, messages);
        if (messages.length() > 0) {
            throw new ScriptException(messages.toString());
        }
    }

    /**
     * Checks if errors will occur with a ScenarioGenerator optimisation run.
     * Checks specifically the metric expressions of the project, and
     * constraints, objective functions, and model parameter expressions from
     * the ScenarioGenerator.
     * @throws ScriptException if any errors are found; the details are
     *  in the exception message.
     */
    @Transactional
    public void checkScenarioGenerator(int projectId, int scenGenId)
            throws ScriptException, ParseException, ConfigurationException, IOException {
        Project project = projectRepository.findOne(projectId);
        ScenarioGenerator scenarioGenerator = scenarioGeneratorRepository.findOne(scenGenId);
        OptimisationProblem problem =
                scenarioGenerationService.loadOptimisationProblem(project, scenarioGenerator);
        checkOptimisationProblem(problem);
    }

    public void checkOptimisationProblem(OptimisationProblem problem) throws ScriptException {
        SyntaxChecker syntaxChecker = new SyntaxChecker(
                simulationService.getEvaluator(), problem.getNamespace(), true);
        StringBuilder messages = new StringBuilder();
        checkProblemInput(problem, syntaxChecker, messages);
        checkCommonProblemParts(problem, syntaxChecker, messages);
        if (messages.length() > 0) {
            throw new ScriptException(messages.toString());
        }
    }

    private void checkCommonProblemParts(OptimisationProblem problem,
            SyntaxChecker syntaxChecker, StringBuilder messages) {
        for (MetricExpression metric : problem.metrics) {
            SyntaxChecker.Error error = syntaxChecker.checkMetricExpression(metric);
            if (error != null) messages.append(error.message).append("\n");
        }
        for (Constraint constraint : problem.constraints) {
            SyntaxChecker.Error error = syntaxChecker.checkConstraintExpression(constraint);
            if (error != null) messages.append(error.message).append("\n");
        }
        for (ObjectiveExpression objective : problem.objectives) {
            SyntaxChecker.Error error = syntaxChecker.checkObjectiveExpression(objective);
            if (error != null) messages.append(error.message).append("\n");
        }
    }

    private void checkProblemInput(OptimisationProblem problem,
            SyntaxChecker syntaxChecker, StringBuilder messages) {
        Namespace namespace = problem.getNamespace();
        Set<Symbol> inputExprSymbols = new HashSet<>();
        for (InputExpression inputExpression : problem.inputExprs) {
            inputExprSymbols.add(inputExpression.getInput());
            SyntaxChecker.Error error = syntaxChecker.checkInputExpression(inputExpression);
            if (error != null) messages.append(error.message).append("\n");
        }
        // Check if all input parameters are given either a constant value or an expression.
        for (Map.Entry<String, Namespace.Component> entry : namespace.components.entrySet()) {
            String componentName = entry.getKey();
            for (String inputName : entry.getValue().inputs.keySet()) {
                if (! (inputExprSymbols.contains(new Symbol(componentName, inputName))
                        || problem.inputConst.get(componentName, inputName) != null)) {
                    messages.append("Input parameter " + componentName + "." + inputName 
                            + " is not defined.\n");
                }
            }
        }
    }
}
