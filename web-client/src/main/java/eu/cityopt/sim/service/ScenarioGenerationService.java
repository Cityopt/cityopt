package eu.cityopt.sim.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.AlgoParam;
import eu.cityopt.model.AlgoParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.ModelParameter;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.AlgorithmParameters;
import eu.cityopt.sim.opt.OptimisationAlgorithm;
import eu.cityopt.sim.opt.OptimisationAlgorithms;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.OptimisationResults;

/**
 * Service to run scenario generation (optimisation) and save the results.
 * Functions as a bridge between the sim-eval, opt-ga and web-client packages,
 * translating between the JPA entities used in web-client and the Java objects
 * used in sim-eval and opt-ga.
 * <p>
 * OptimisationAlgorithm implementations are found by scanning the class path.
 * By default the package eu.cityopt and its subpackages are scanned.
 * 
 * @author Hannu Rummukainen
 */
@Service
public class ScenarioGenerationService
        implements InitializingBean, ApplicationListener<ContextClosedEvent> {
    @Autowired private ExecutorService executorService;
    @Autowired private SimulationService simulationService;
    @Autowired private ScenarioGeneratorRepository scenarioGeneratorRepository; 
    @Autowired private OptimisationSupport optimisationSupport;

    private static Logger log = Logger.getLogger(ScenarioGenerationService.class); 

    private JobManager<OptimisationResults> jobManager = new JobManager<>();

    private List<String> packagesToScan = Arrays.asList("eu.cityopt");

    public void setAlgorithmPackagesToScan(List<String> values) {
        packagesToScan = values;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(OptimisationAlgorithm.class));
        for (String packageName : packagesToScan) {
            for (BeanDefinition component : provider.findCandidateComponents(packageName)) {
                @SuppressWarnings("unchecked")
                Class<OptimisationAlgorithm> cls = (Class<OptimisationAlgorithm>)
                        Class.forName(component.getBeanClassName());
                OptimisationAlgorithm algorithm = cls.newInstance();
                OptimisationAlgorithms.register(algorithm);
            }
        }
    }

    /**
     * Starts a scenario generation run in the background.
     *
     * @param scenGenId id of a ScenarioGenerator instance that determines what
     *   is computed.
     * @param userId will be put in the createdby field of the generated scenarios
     * @return a future that indicates when the run is done.  The results are also
     *   stored in the database.
     * @throws ConfigurationException if there is an error in algorithm parameters
     * @throws ParseException if the external or input parameter values cannot be parsed
     * @throws ScriptException if e.g. input expressions or metric expressions are invalid
     * @throws IOException on other I/O failures
     */
    @Transactional
    public Future<OptimisationResults> startOptimisation(int scenGenId, Integer userId)
            throws ConfigurationException, ParseException, ScriptException, IOException {
        if (jobManager.isShutdown()) {
            throw new IllegalStateException("Service shutting down");
        }
        ScenarioGenerator scenarioGenerator = scenarioGeneratorRepository.findOne(scenGenId);
        String runName = "SG" + scenarioGenerator.getScengenid();
        Project project = scenarioGenerator.getProject();

        String algorithmName = scenarioGenerator.getAlgorithm().getDescription();
        OptimisationAlgorithm optimisationAlgorithm = OptimisationAlgorithms.get(algorithmName);
        AlgorithmParameters algorithmParameters = loadAlgorithmParameters(scenarioGenerator);

        Namespace namespace = simulationService.makeProjectNamespace(project, scenarioGenerator);
        ExternalParameters externals = simulationService.loadExternalParametersFromSet(
                scenarioGenerator.getExtparamvalset(), namespace);

        SimulationModel model = simulationService.loadSimulationModel(project);
        OptimisationProblem problem = new OptimisationProblem(model, externals);
        problem.decisionVars = loadDecisionVariables(scenarioGenerator, namespace);
        problem.inputExprs = loadInputExpressions(scenarioGenerator, namespace, problem.inputConst);
        problem.metrics = simulationService.loadMetricExpressions(project, namespace);
        problem.constraints = optimisationSupport.loadConstraints(scenarioGenerator, namespace);
        problem.objectives = optimisationSupport.loadObjectives(scenarioGenerator, namespace);

        DbSimulationStorageI storage = simulationService.makeDbSimulationStorage(
                project.getPrjid(), externals, scenarioGenerator.getScengenid(), userId);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream(); 
        CompletableFuture<OptimisationResults> optimisationJob = optimisationAlgorithm.start(
                problem, algorithmParameters, storage, runName, messageStream, executorService);
        CompletableFuture<OptimisationResults> finishJob = optimisationJob.whenCompleteAsync(
                (results, throwable) -> {
                    String messages = messageStream.toString();
                    if (throwable != null) {
                        messages += "\nOptimisation terminated: " + throwable.getMessage();
                    }
                    storage.saveScenarioGeneratorStatus(scenGenId, results, messages);
                }, executorService);
        jobManager.putJob(scenGenId, finishJob);
        // If finishJob is cancelled, we need to cancel optimisationJob. 
        finishJob.whenComplete((result, throwable) -> {
            if (finishJob.isCancelled()) {
                optimisationJob.cancel(true);
            }
            jobManager.removeJob(scenGenId, finishJob);
            try {
                model.close();
            } catch (IOException e) {
                log.warn("Failed to clean up optimisation run: " + e.getMessage());
            }
        });
        return finishJob;
    }

    /**
     * Returns set of currently ongoing optimisation runs.
     * The result is a snapshot of the situation at the time the method is called.
     * @return set of ScenarioGenerator entity ids
     */
    public Set<Integer> getRunningOptimisations() {
        return jobManager.getJobIds();
    }

    /**
     * Cancels an ongoing optimisation run.
     * @param scenId ScenarioGenerator entity id
     * @return true if the run was cancelled.  Returns false if there is
     *  no run to cancel, or the run has already completed, or the
     *  cancellation fails for some other reason.
     */
    public boolean cancelOptimisation(int scenGenId) {
        return jobManager.cancelJob(scenGenId);
    }

    AlgorithmParameters loadAlgorithmParameters(ScenarioGenerator scenarioGenerator) {
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();
        int algoId = scenarioGenerator.getAlgorithm().getAlgorithmid();
        for (AlgoParamVal algoParamVal : scenarioGenerator.getAlgoparamvals()) {
            AlgoParam algoParam = algoParamVal.getAlgoparam();
            if (algoParam.getAlgorithm().getAlgorithmid() == algoId) {
                String parameterName = algoParamVal.getAlgoparam().getName();
                algorithmParameters.put(parameterName, algoParamVal.getValue());
            } else {
                log.warn("Ignoring AlgoParam for wrong Algorithm in ScenarioGenerator "
                        + scenarioGenerator.getScengenid());
            }
        }
        return algorithmParameters;
    }

    public List<DecisionVariable> loadDecisionVariables(
            ScenarioGenerator scenarioGenerator, Namespace namespace)
                    throws ScriptException, ParseException {
        List<DecisionVariable> simDecisions = new ArrayList<>();
        for (eu.cityopt.model.DecisionVariable decisionVariable 
                : scenarioGenerator.getDecisionvariables()) {
            String componentName = null;
            String variableName = null;
            InputParameter inputParameter = decisionVariable.getInputparameter();
            if (inputParameter != null) {
                componentName = inputParameter.getComponent().getName();
                variableName = inputParameter.getName();
            } else {
                variableName = decisionVariable.getName();
            }
            DecisionDomain domain = null;
            Type variableType = namespace.getDecisionType(componentName, variableName);
            String lbText = decisionVariable.getLowerbound();
            String ubText = decisionVariable.getUpperbound();
            Object lb = lbText != null ? variableType.parse(lbText, namespace) : null;
            Object ub = (ubText != null) ? variableType.parse(ubText, namespace) : null;
            domain = NumericInterval.makeInterval(variableType, lb, ub);

            simDecisions.add(new DecisionVariable(componentName, variableName, domain));
        }
        Collections.sort(simDecisions, (d, e) -> d.toString().compareTo(e.toString()));
        return simDecisions;
    }

    public List<InputExpression> loadInputExpressions(
            ScenarioGenerator scenarioGenerator, Namespace namespace,
            SimulationInput constantInputHolder) throws ScriptException, ParseException {
        List<InputExpression> simInputExpressions = new ArrayList<>();
        for (ModelParameter modelParameter : scenarioGenerator.getModelparameters()) {
            InputParameter inputParameter = modelParameter.getInputparameter();
            String componentName = inputParameter.getComponent().getName();
            String inputName = inputParameter.getName();
            Namespace.Component nsComponent = namespace.components.get(componentName); 
            Type inputType = nsComponent.inputs.get(inputParameter.getName());
            String valueText = modelParameter.getValue();
            if (valueText != null) {
                Object value = inputType.parse(valueText, namespace);
                constantInputHolder.put(componentName, inputName, value);
            } else {
                InputExpression simExpression =
                        new InputExpression(componentName, inputName,
                                modelParameter.getExpression(), namespace.evaluator);
                simInputExpressions.add(simExpression);
            }
        }
        return simInputExpressions;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Shutting down.");
        jobManager.shutdown();
    }
}
