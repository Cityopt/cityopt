package eu.cityopt.sim.service;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
import eu.cityopt.model.Algorithm;
import eu.cityopt.model.Component;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.ModelParameter;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.AlgoParamValRepository;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.DecisionVariableRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.InputParameterRepository;
import eu.cityopt.repository.ModelParameterRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.Symbol;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.AlgorithmParameters;
import eu.cityopt.sim.opt.OptimisationAlgorithm;
import eu.cityopt.sim.opt.OptimisationAlgorithms;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.OptimisationResults;
import eu.cityopt.sim.opt.OptimisationStateListener;
import eu.cityopt.sim.opt.Solution;

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
    @Autowired private OptimisationSupport optimisationSupport;
    @Autowired private SyntaxCheckerService syntaxCheckerService;

    @Autowired private ScenarioGeneratorRepository scenarioGeneratorRepository; 
    @Autowired private ComponentRepository componentRepository;
    @Autowired private InputParameterRepository inputParameterRepository;
    @Autowired private TypeRepository typeRepository;
    @Autowired private DecisionVariableRepository decisionVariableRepository;
    @Autowired private ModelParameterRepository modelParameterRepository;
    @Autowired private AlgoParamValRepository algoParamValRepository;
    @Autowired private ExtParamValSetRepository extParamValSetRepository;
    @Autowired private TimeSeriesValRepository timeSeriesValRepository;

    private static Logger log = Logger.getLogger(ScenarioGenerationService.class); 

    private JobManager<OptimisationResults> jobManager = new JobManager<>();

    private List<String> packagesToScan = Arrays.asList("eu.cityopt");

    private Map<Integer, Listener> listeners = new ConcurrentHashMap<>();

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

    static class Listener implements OptimisationStateListener {
        RunInfo info = new RunInfo();
        Object messageMutex = new Object();
        StringBuilder messages = new StringBuilder();

        volatile String progressState = "started";

        Listener(Instant started, Instant deadline) {
            info.started = started;
            info.deadline = deadline;
        }

        @Override
        public void logMessage(String text) {
            synchronized (messageMutex) {
                messages.append(text);
                if ( ! text.endsWith("\n")) {
                    messages.append('\n');
                }
            }
        }

        String getMessages() {
            synchronized (messageMutex) {
                return messages.toString();
            }
        }

        @Override
        public void setProgressState(String state) {
            progressState = state;
        }

        @Override
        public void updateParetoFront(Iterator<Solution> solutions) {
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
        String runName = scenarioGenerator.getName();
        if (runName == null) {
            runName = "SG" + scenarioGenerator.getScengenid();
        }
        Project project = scenarioGenerator.getProject();

        String algorithmName = scenarioGenerator.getAlgorithm().getDescription();
        OptimisationAlgorithm optimisationAlgorithm = OptimisationAlgorithms.get(algorithmName);
        AlgorithmParameters algorithmParameters = loadAlgorithmParameters(scenarioGenerator);

        OptimisationProblem problem = loadOptimisationProblem(project, scenarioGenerator);
        final SimulationModel model = problem.model;

        syntaxCheckerService.checkOptimisationProblem(problem);

        DbSimulationStorageI storage = simulationService.makeDbSimulationStorage(
                project.getPrjid(), problem.getExternalParameters(),
                userId, scenarioGenerator.getScengenid());

        Instant started = Instant.now();
        Instant deadline = started.plus(algorithmParameters.getMaxRunTime());
        Listener listener = new Listener(started, deadline);
        CompletableFuture<OptimisationResults> optimisationJob = optimisationAlgorithm.start(
                problem, algorithmParameters, storage,
                runName, listener, deadline, executorService);
        CompletableFuture<OptimisationResults> finishJob = optimisationJob.whenCompleteAsync(
                (results, throwable) -> {
                    String messages = listener.getMessages();
                    if (throwable != null) {
                        messages += "\nOptimisation terminated: " + throwable.getMessage();
                    }
                    storage.saveScenarioGeneratorResults(results, messages);
                }, executorService);
        jobManager.putJob(scenGenId, finishJob);
        listeners.put(scenGenId, listener);
        // If finishJob is cancelled, we need to cancel optimisationJob. 
        finishJob.whenComplete((result, throwable) -> {
            if (finishJob.isCancelled()) {
                optimisationJob.cancel(true);
            }
            jobManager.removeJob(scenGenId, finishJob);
            listeners.remove(scenGenId, listener);
            try {
                model.close();
            } catch (IOException e) {
                log.warn("Failed to clean up optimisation run: " + e.getMessage());
            }
        });
        return finishJob;
    }

    /**
     * Information about an ongoing optimisation run.
     * @see ScenarioGenerationService#getRunningOptimisations()
     */
    public static class RunInfo {
        /** When the run was started. */
       Instant started;

        /**
         * When the run should be ending. Derived from the start time and the
         * configured maximum run time.
         */
       Instant deadline;

       /** Short status string, e.g. "iteration 43" */
       String status;
    }

    /**
     * Returns the status of currently ongoing optimisation runs.
     * The result is a snapshot of the situation at the time the method is called.
     * @return map from ScenarioGenerator entity id to {@link RunInfo} 
     */
    public Map<Integer, RunInfo> getRunningOptimisations() {
        Map<Integer, RunInfo> statusMap = new HashMap<>();
        for (Map.Entry<Integer, Listener> entry : listeners.entrySet()) {
            Listener listener = entry.getValue();
            RunInfo runInfo = new RunInfo();
            runInfo.started = listener.info.started;
            runInfo.deadline = listener.info.deadline;
            runInfo.status = listener.progressState;
            statusMap.put(entry.getKey(), runInfo);
        }
        return statusMap;
    }

    /**
     * Cancels an ongoing optimisation run.
     * @param scenId ScenarioGenerator entity id
     * @return true if the run was cancelled.  Returns false if there is
     *  no run to cancel, or the run has already completed, or the
     *  cancellation fails for some other reason.
     */
    public boolean cancelOptimisation(int scenGenId) {
        boolean canceled = jobManager.cancelJob(scenGenId);
        listeners.remove(scenGenId);
        return canceled;
    }

    OptimisationProblem loadOptimisationProblem(
            Project project, ScenarioGenerator scenarioGenerator)
                    throws ScriptException, ParseException, ConfigurationException, IOException {
        SimulationModel model = simulationService.loadSimulationModel(project);
        Namespace namespace = simulationService.makeProjectNamespace(project, scenarioGenerator);
        ExternalParameters externals = simulationService.loadExternalParametersFromSet(
                scenarioGenerator.getExtparamvalset(), namespace);
        OptimisationProblem problem = new OptimisationProblem(model, externals);
        problem.decisionVars = loadDecisionVariables(scenarioGenerator, namespace);
        problem.inputExprs = loadInputExpressions(scenarioGenerator, namespace, problem.inputConst);
        problem.metrics = simulationService.loadMetricExpressions(project, namespace);
        problem.constraints = optimisationSupport.loadConstraints(scenarioGenerator, namespace);
        problem.objectives = optimisationSupport.loadObjectives(scenarioGenerator, namespace);
        return problem;
    }

    public void saveOptimisationProblem(
            OptimisationProblem problem, ScenarioGenerator scenarioGenerator) {
        Namespace namespace = problem.getNamespace();

        List<Runnable> idUpdateList = new ArrayList<>();
        simulationService.saveExternalParameterValues(
                scenarioGenerator.getProject(), problem.getExternalParameters(),
                null, scenarioGenerator, idUpdateList);
        Map<String, Map<String, InputParameter>> inputParameterMap =
                getInputParameterMap(scenarioGenerator.getProject());
        saveDecisionVariables(
                scenarioGenerator, problem.decisionVars, inputParameterMap, namespace);
        saveInputExpressions(
                scenarioGenerator, problem.inputExprs, inputParameterMap);
        saveConstantInput(
                scenarioGenerator, problem.inputConst, inputParameterMap, namespace);
        optimisationSupport.saveConstraints(
                scenarioGenerator, problem.constraints, namespace);
        optimisationSupport.saveObjectives(
                scenarioGenerator, problem.objectives);

        timeSeriesValRepository.flush();
        extParamValSetRepository.flush();
        for (Runnable update : idUpdateList) {
            update.run();
        }
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

    void saveAlgorithmParameters(ScenarioGenerator scenarioGenerator,
            Algorithm algorithm, AlgorithmParameters algorithmParameters) {
        for (AlgoParam algoParam : algorithm.getAlgoparams()) {
            String value = algorithmParameters.getProperty(algoParam.getName());
            if (value != null) {
                AlgoParamVal algoParamVal = new AlgoParamVal();
                algoParamVal.setAlgoparam(algoParam);
                algoParamVal.setValue(value);

                algoParamVal.setScenariogenerator(scenarioGenerator);
                scenarioGenerator.getAlgoparamvals().add(algoParamVal);
            }
        }
        algoParamValRepository.save(scenarioGenerator.getAlgoparamvals());
    }

    public List<DecisionVariable> loadDecisionVariables(
            ScenarioGenerator scenarioGenerator, Namespace namespace)
                    throws ScriptException, ParseException {
        List<DecisionVariable> simDecisions = new ArrayList<>();
        for (eu.cityopt.model.DecisionVariable decisionVariable 
                : scenarioGenerator.getDecisionvariables()) {
            Symbol symbol = getDecisionVariableSymbol(decisionVariable);
            DecisionDomain domain = null;
            Type variableType = namespace.getDecisionType(symbol.componentName, symbol.name);
            String lbText = decisionVariable.getLowerbound();
            String ubText = decisionVariable.getUpperbound();
            Object lb = lbText != null ? variableType.parse(lbText, namespace) : null;
            Object ub = (ubText != null) ? variableType.parse(ubText, namespace) : null;
            domain = NumericInterval.makeInterval(variableType, lb, ub);

            simDecisions.add(new DecisionVariable(symbol.componentName, symbol.name, domain));
        }
        Collections.sort(simDecisions, (d, e) -> d.toString().compareTo(e.toString()));
        return simDecisions;
    }

    public Symbol getDecisionVariableSymbol(eu.cityopt.model.DecisionVariable decisionVariable) {
        String componentName = null;
        String variableName = null;
        InputParameter inputParameter = decisionVariable.getInputparameter();
        if (inputParameter != null) {
            componentName = inputParameter.getComponent().getName();
            variableName = inputParameter.getName();
        } else {
            variableName = decisionVariable.getName();
        }
        return new Symbol(componentName, variableName);
    }

    public void saveDecisionVariables(
            ScenarioGenerator scenarioGenerator, List<DecisionVariable> simDecisions,
            Map<String, Map<String, InputParameter>> inputParameterMap, EvaluationSetup setup) {
        for (DecisionVariable simDecision : simDecisions) {
            eu.cityopt.model.DecisionVariable decisionVariable =
                    new eu.cityopt.model.DecisionVariable();
            if (simDecision.componentName != null) {
                Map<String, InputParameter> localMap =
                        inputParameterMap.get(simDecision.componentName);
                InputParameter inputParameter = 
                        (localMap != null) ? localMap.get(simDecision.name) : null;
                if (inputParameter != null) {
                    decisionVariable.setInputparameter(inputParameter);
                } else {
                    log.warn("Cannot properly save decision variable " + simDecision 
                            + " - no matching input variable.");
                }
            }
            decisionVariable.setName(simDecision.name);

            Type valueType = simDecision.domain.getValueType();
            decisionVariable.setType(typeRepository.findByNameLike(valueType.name));
            switch (valueType) {
            case INTEGER: {
                @SuppressWarnings("unchecked")
                NumericInterval<Integer> interval = (NumericInterval<Integer>) simDecision.domain;
                decisionVariable.setLowerbound(valueType.format(interval.getLowerBound(), setup));
                decisionVariable.setUpperbound(valueType.format(interval.getUpperBound(), setup));
                break;
            }
            case DOUBLE:
            case TIMESTAMP: {
                @SuppressWarnings("unchecked")
                NumericInterval<Double> interval = (NumericInterval<Double>) simDecision.domain;
                decisionVariable.setLowerbound(valueType.format(interval.getLowerBound(), setup));
                decisionVariable.setUpperbound(valueType.format(interval.getUpperBound(), setup));
                break;
            }
            default:
                log.warn("Cannot properly save decision variable " + simDecision
                        + " - unrecognized value type " + valueType);
            }
            decisionVariable.setScenariogenerator(scenarioGenerator);
            scenarioGenerator.getDecisionvariables().add(decisionVariable);
        }
        decisionVariableRepository.save(scenarioGenerator.getDecisionvariables());
    }

    Map<String, Map<String, InputParameter>> getInputParameterMap(Project project) {
        Map<String, Map<String, InputParameter>> globalMap = new HashMap<>();
        for (Component component : project.getComponents()) {
            Map<String, InputParameter> localMap = new HashMap<>();
            for (InputParameter inputParameter : component.getInputparameters()) {
                localMap.put(inputParameter.getName(), inputParameter);
            }
            globalMap.put(component.getName(), localMap);
        }
        return globalMap;
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

    public void saveInputExpressions(
            ScenarioGenerator scenarioGenerator, Collection<InputExpression> inputExpressions,
            Map<String, Map<String, InputParameter>> inputParameterMap) {
        for (InputExpression inputExpression : inputExpressions) {
            ModelParameter modelParameter = new ModelParameter();

            Map<String, InputParameter> localMap =
                    inputParameterMap.get(inputExpression.getInput().componentName);
            InputParameter inputParameter = (localMap != null)
                    ? localMap.get(inputExpression.getInput().name) : null;
            if (inputParameter != null) {
                modelParameter.setInputparameter(inputParameter);
            } else {
                log.warn("Cannot properly input expression for " + inputExpression 
                        + " - no matching input variable.");
            }
            modelParameter.setExpression(inputExpression.getSource());

            modelParameter.setScenariogenerator(scenarioGenerator);
            scenarioGenerator.getModelparameters().add(modelParameter);
        }
        modelParameterRepository.save(scenarioGenerator.getModelparameters());
    }

    public void saveConstantInput(
            ScenarioGenerator scenarioGenerator, SimulationInput constantInput,
            Map<String, Map<String, InputParameter>> inputParameterMap, Namespace namespace) {
        for (Component component : scenarioGenerator.getProject().getComponents()) {
            for (InputParameter inputParameter : component.getInputparameters()) {
                Object value = constantInput.get(component.getName(), inputParameter.getName());
                if (value != null) {
                    ModelParameter modelParameter = new ModelParameter();
                    modelParameter.setInputparameter(inputParameter);
                    Type type = namespace.getInputType(
                            component.getName(), inputParameter.getName());
                    String valueString = type.format(value, namespace);
                    modelParameter.setValue(valueString);

                    modelParameter.setScenariogenerator(scenarioGenerator);
                    scenarioGenerator.getModelparameters().add(modelParameter);
                }
            }
        }
        modelParameterRepository.save(scenarioGenerator.getModelparameters());
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Shutting down.");
        jobManager.shutdown();
    }
}
