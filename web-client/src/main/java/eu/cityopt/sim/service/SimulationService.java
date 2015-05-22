package eu.cityopt.sim.service;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Component;
import eu.cityopt.model.DecisionVariable;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.ExtParamValRepository;
import eu.cityopt.repository.ExtParamValSetCompRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.eval.util.TimeUtils;

/**
 * Runs simulations based on project and scenario data, and saves the results.
 * This is essentially a bridge between the sim-eval and web-client packages,
 * translating between the JPA entities used in web-client and the Java objects
 * used in sim-eval.
 * <p>
 * We use an injected ExecutorService to perform computations and store results in
 * the background. The actual simulations are run in separate processes.
 * 
 * @author Hannu Rummukainen
 */
@Service
public class SimulationService implements ApplicationListener<ContextClosedEvent> {
    private static final Instant DEFAULT_TIME_ORIGIN = Instant.ofEpochMilli(0);

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_MODEL_FAILURE = "MODEL_FAILURE";
    public static final String STATUS_SIMULATOR_FAILURE = "SIMULATOR_FAILURE";

    private static Logger log = Logger.getLogger(SimulationService.class); 

    @Autowired private SyntaxCheckerService syntaxCheckerService;

    @Autowired private ProjectRepository projectRepository;
    @Autowired private ScenarioRepository scenarioRepository;
    @Autowired private TypeRepository typeRepository;
    @Autowired private ExtParamValRepository extParamValRepository;
    @Autowired private ExtParamValSetRepository extParamValSetRepository;
    @Autowired private ExtParamValSetCompRepository extParamValSetCompRepository;
    @Autowired private TimeSeriesRepository timeSeriesRepository;
    @Autowired private TimeSeriesValRepository timeSeriesValRepository;
    @Autowired private ScenarioGeneratorRepository scenarioGeneratorRepository;

    @Autowired private ApplicationContext applicationContext;
    @Autowired private ExecutorService executorService;

    private JobManager<SimulationOutput> jobManager = new JobManager<>();

    private Evaluator evaluator = new Evaluator();

    public Evaluator getEvaluator() {
        return evaluator;
    }

    /**
     * Starts a simulation run for a scenario. Reads the input parameter values
     * from the scenario data, and also reads the current metric expressions and
     * the default values of external parameters. Then the simulation is run
     * asynchronously, and the output variable values, metric values and the
     * used external parameter values will be automatically stored when the
     * simulation has completed.
     *
     * @param scenId
     *            scenario identifier. If there is already an ongoing simulation
     *            for the scenario, the old simulation is cancelled a new one is
     *            started.
     * @return a Future that can be used to e.g. check for run completion, if
     *         necessary.
     */
    @Transactional
    public Future<SimulationOutput> startSimulation(int scenId)
            throws ParseException, IOException, ConfigurationException, ScriptException {
        return startSimulation(scenId, executorService);
    }

    Future<SimulationOutput> startSimulation(int scenId, Executor executor)
            throws ParseException, IOException, ConfigurationException, ScriptException {
        if (jobManager.isShutdown()) {
            throw new IllegalStateException("Service shutting down");
        }
        Scenario scenario = scenarioRepository.findOne(scenId);
        Project project = scenario.getProject();
        Namespace namespace = makeProjectNamespace(project);

        ExternalParameters externals = loadExternalParametersFromSet(
                project.getExtparamvalset(), namespace);
        SimulationInput input = loadSimulationInput(scenario, externals);
        SimulationStorage storage = makeDbSimulationStorage(project.getPrjid(), externals);

        SimulationModel model = loadSimulationModel(project);
        boolean started = false;
        try {
            List<MetricExpression> metricExpressions = loadMetricExpressions(project, namespace);
            syntaxCheckerService.checkMetricExpressions(metricExpressions, namespace);
            SimulationRunner runner = model.getSimulatorManager().makeRunner(model, input.getNamespace());
            CompletableFuture<SimulationOutput> simJob = runner.start(input);
            started = true;
            CompletableFuture<SimulationOutput> finishJob = simJob.whenCompleteAsync(
                    // Store output & metric values
                    (output, throwable) -> {
                        if (output != null) {
                            SimulationStorage.Put put = new SimulationStorage.Put(input);
                            put.output = output;
                            if (output instanceof SimulationResults) {
                                SimulationResults results = (SimulationResults) output;
                                put.metricValues = new MetricValues(results);
                                for (MetricExpression metric : metricExpressions) {
                                    try {
                                        put.metricValues.evaluate(metric);
                                    } catch (ScriptException e) {
                                        log.warn(e.getMessage());
                                    }
                                }
                            }
                            storage.put(put);
                        }
                    }, executor);
            jobManager.putJob(scenId, finishJob);
            finishJob.whenComplete(
                    // Clean up
                    (output, throwable) -> {
                        // Propagate cancellation of finishJob back to simJob
                        if (finishJob.isCancelled()) {
                            simJob.cancel(true);
                        }
                        jobManager.removeJob(scenId, finishJob);
                        try {
                            try {
                                runner.close();
                            } finally {
                                model.close();
                            }
                        } catch (IOException e) {
                            log.warn("Failed to clean up simulation run: " + e.getMessage());
                        }
                    });
            return finishJob;
        } finally {
            if (!started) model.close();
        }
    }

    /**
     * Returns set of scenarios for which a simulation is currently running.
     * The result is a snapshot of the situation at the time the method is called.
     * @return set of scenario ids
     */
    public Set<Integer> getRunningSimulations() {
        return jobManager.getJobIds();
    }

    /**
     * Cancels an ongoing simulation.
     * @param scenId scenario id
     * @return true if the simulation was cancelled.  Returns false if there is
     *  no simulation to cancel, or the simulation has already completed, or
     *  the cancellation fails for some other reason.
     */
    public boolean cancelSimulation(int scenId) {
        return jobManager.cancelJob(scenId);
    }

    /** Results from {@link SimulationService#updateMetricValues(int, Integer)} */
    public static class MetricUpdateStatus {
        /** Set of scenarios for which new metric values were computed. */
        public Set<Integer> updated = new HashSet<Integer>();

        /**
         * Set of scenarios with no or incomplete results (or input), and thus
         * no metric values.
         */
        public Set<Integer> ignored = new HashSet<Integer>();

        /** Map from Scenario id to Exception encountered in metric update */
        public Map<Integer, Exception> failures = new HashMap<Integer, Exception>();

        /**
         * Id of ExtParamValSet.  If the corresponding parameter to
         * updateMetricValues was provided, this is its value.
         * Otherwise this is the id of a newly generated ExtParamValSet.
         * However, this can be null if no metrics were updated.
         */
        public Integer extParamValSetId;

        /** Brief human-readable description. */
        public String toString() {
            return updated.size() + " scenarios updated, "
                    + ignored.size() + " scenarios had no results, "
                    + failures.size() + " scenarios failed.";
            
        }
    }

    /**
     * Updates scenario metrics using specific external parameter values.
     * Creates new ScenarioMetrics rows for all scenarios in a project.
     * 
     * @param projectId project identifier
     * @param extParamValSetId external parameter value set identifier,
     *    or null, in which case default values of the external parameters
     *    will be used.
     * @throws ParseException if external parameter values are invalid
     * @throws ScriptException if metric expressions are badly invalid
     * @return scenario specific results
     */
    @Transactional
    public MetricUpdateStatus updateMetricValues(int projectId, Integer extParamValSetId)
            throws ParseException, ScriptException {
        Project project = projectRepository.findOne(projectId);
        Namespace namespace = makeProjectNamespace(project);
        ExternalParameters externals = loadExternalParameters(project, extParamValSetId, namespace);
        List<MetricExpression> metricExpressions = loadMetricExpressions(project, namespace);
        syntaxCheckerService.checkMetricExpressions(metricExpressions, namespace);
        SimulationStorage storage = makeDbSimulationStorage(projectId, externals);
        MetricUpdateStatus status = new MetricUpdateStatus();
        //TODO: first remove all metric values associated with the external parameter value set
        for (Scenario scenario : project.getScenarios()) {
            try {
                SimulationInput input = loadSimulationInput(scenario, externals);
                if (input.isComplete()) {
                    SimulationOutput output = loadSimulationOutput(scenario, input);
                    if (output instanceof SimulationResults) {
                        SimulationResults results = (SimulationResults) output;
                        MetricValues metricValues = new MetricValues(results, metricExpressions);
                        storage.updateMetricValues(metricValues);
                        status.updated.add(scenario.getScenid());
                    } else {
                        status.ignored.add(scenario.getScenid());
                    }
                } else {
                    status.ignored.add(scenario.getScenid());
                }
            } catch (ParseException | ScriptException e) {
                log.warn("Failed to update metrics of scenario " + scenario.getScenid()
                        + ": " + e.getMessage());
                status.failures.put(scenario.getScenid(), e);
            }
        }
        status.extParamValSetId = externals.getExternalId();
        log.info("Updated scenario metrics for project " + projectId + ": " + status);
        return status;
    }

    /** Loads the simulation model from a project. */
    public SimulationModel loadSimulationModel(Project project)
            throws ConfigurationException, IOException {
        String simulatorName = project.getSimulationmodel().getSimulator();
        SimulatorManager manager = SimulatorManagers.get(simulatorName);
        byte[] modelZipBytes = project.getSimulationmodel().getModelblob();
        return manager.parseModel(simulatorName, modelZipBytes);
    }

    /**
     * Loads external parameters from either an external parameter value set, or
     * the project-specific default values.
     * @param projectId
     * @param extParamValSetId either an identifer of an external parameter value
     *    set, or null, in which case the default values are read.
     * @return object containing the external parameter values.
     */
    @Transactional
    public ExternalParameters loadExternalParameters(int projectId, Integer extParamValSetId)
                throws ParseException {
        Project project = projectRepository.findOne(projectId);
        Namespace namespace = makeProjectNamespace(project);
        return loadExternalParameters(project, extParamValSetId, namespace);
    }

    /**
     * Loads external parameters from either an external parameter value set, or
     * the project-specific default values.
     * @param project
     * @param extParamValSetId either an identifer of an external parameter value
     *    set, or null, in which case the default values are read.
     * @param namespace the project namespace
     * @return object containing the external parameter values.
     */
    public ExternalParameters loadExternalParameters(
            Project project, Integer extParamValSetId, Namespace namespace)
                throws ParseException {
        if (extParamValSetId != null) {
            ExtParamValSet extParamValSet = extParamValSetRepository.findOne(extParamValSetId);
            return loadExternalParametersFromSet(extParamValSet, namespace);
        } else {
            return loadExternalParametersFromSet(project.getExtparamvalset(), namespace);
        }
    }

    /** Loads an external parameter value set. */
    public ExternalParameters loadExternalParametersFromSet(
            ExtParamValSet extParamValSet, Namespace namespace) throws ParseException {
        ExternalParameters simExternals = new ExternalParameters(namespace);
        simExternals.setExternalId(extParamValSet.getExtparamvalsetid());
        for (ExtParamValSetComp extParamValSetComp : extParamValSet.getExtparamvalsetcomps()) {
            ExtParamVal extParamVal = extParamValSetComp.getExtparamval(); 
            String extName = extParamVal.getExtparam().getName();
            Type extType = namespace.externals.get(extName);
            Object simValue;
            if (extType.isTimeSeriesType()) {
                simValue = loadTimeSeries(extParamVal.getTimeseries(), extType,
                        namespace.evaluator, namespace.timeOrigin);
            } else {
                simValue = extType.parse(extParamVal.getValue(), namespace);
            }
            simExternals.put(extName, simValue);
        }
        return simExternals;
    }

    /** Loads the data of a time series. */
    public TimeSeriesI loadTimeSeries(TimeSeries timeseries, 
            Type timeSeriesType, Evaluator evaluator, Instant timeOrigin) {
        List<TimeSeriesVal> timeSeriesVals =
                timeSeriesValRepository.findTimeSeriesValOrderedByTime(timeseries.getTseriesid());
        int n = timeSeriesVals.size();
        double[] times = new double[n];
        double[] values = new double[n];
        for (int i = 0; i < n; ++i) {
            TimeSeriesVal tsVal = timeSeriesVals.get(i);
            times[i] = TimeUtils.toSimTime(tsVal.getTime(), timeOrigin);
            values[i] = Double.valueOf(tsVal.getValue());
        }
        TimeSeriesI ts = evaluator.makeTS(timeSeriesType, times, values);
        ts.setTimeSeriesId(timeseries.getTseriesid());
        return ts;
    }

    /**
     * Loads the simulation input parameter values of a scenario.
     * @param scenarioId
     * @param simExternals external parameter values to be used when evaluating expressions
     * @return object containing the simulation input values
     */
    @Transactional
    public SimulationInput loadSimulationInput(int scenarioId, ExternalParameters simExternals)
            throws ParseException {
        Scenario scenario = scenarioRepository.findOne(scenarioId);
        return loadSimulationInput(scenario, simExternals);
    }

    /** Loads the simulation input parameter values of a scenario. */
    public SimulationInput loadSimulationInput(Scenario scenario, ExternalParameters simExternals) 
            throws ParseException {
        SimulationInput simInput = new SimulationInput(simExternals);
        for (InputParamVal mValue : scenario.getInputparamvals()) {
            InputParameter mInput = mValue.getInputparameter();
            String componentName = mInput.getComponent().getName();
            simInput.putString(componentName, mInput.getName(), mValue.getValue());
        }
        simInput.setScenarioId(scenario.getScenid());
        return simInput;
    }

    /**
     * Loads the simulation result data of a scenario.
     * @param scenarioId
     * @param simInput corresponding input data to be used when evaluating expressions
     * @return either a SimulationResults containing result data, or a SimulationFailure
     *   containing error information
     */
    @Transactional
    public SimulationOutput loadSimulationOutput(int scenarioId, SimulationInput simInput)
            throws ParseException {
        Scenario scenario = scenarioRepository.findOne(scenarioId);
        return loadSimulationOutput(scenario, simInput);
    }

    /** Loads the simulation result data of a scenario. */
    public SimulationOutput loadSimulationOutput(Scenario scenario, SimulationInput simInput)
            throws ParseException {
        SimulationOutput simOutput = null;
        if (STATUS_SUCCESS.equals(scenario.getStatus())) {
            Namespace namespace = simInput.getNamespace();
            SimulationResults simResults = new SimulationResults(simInput, scenario.getLog());
            for (SimulationResult mResult : scenario.getSimulationresults()) {
                OutputVariable mOutput = mResult.getOutputvariable();
                String componentName = mOutput.getComponent().getName();
                Namespace.Component nsComponent = namespace.components.get(componentName);
                if (nsComponent != null) {
                    Type outputType = nsComponent.outputs.get(mOutput.getName());
                    if (outputType != null) {
                        Object simValue = loadTimeSeries(mResult.getTimeseries(), outputType,
                                namespace.evaluator, namespace.timeOrigin);
                        simResults.put(componentName, mOutput.getName(), simValue);
                    }
                }
            }
            if (simResults.isComplete()) {
                simOutput = simResults;
            } else {
                // Note: The run start and end times are left null.
                return new SimulationFailure(
                        simInput, false, "Missing simulation results in database",
                        "Could not find simulation results for all output variables in database.");
            }
        } else {
            boolean permanent = (STATUS_MODEL_FAILURE.equals(scenario.getStatus()));
            simOutput = new SimulationFailure(
                    simInput, permanent, scenario.getStatus(), scenario.getLog());
        }
        if (scenario.getRunstart() != null) {
            simOutput.runStart = scenario.getRunstart().toInstant(); 
        }
        if (scenario.getRunend() != null) {
            simOutput.runEnd = scenario.getRunend().toInstant();
        }
        return simOutput;
    }

    //TODO loadMetricValues

    /** Loads the metric expressions of a project. */
    public List<MetricExpression> loadMetricExpressions(Project project, Namespace namespace)
            throws ScriptException {
        List<MetricExpression> metricExpressions = new ArrayList<MetricExpression>();
        for (Metric mMetric : project.getMetrics()) {
            metricExpressions.add(new MetricExpression(
                    mMetric.getMetid(), mMetric.getName(), mMetric.getExpression(),
                    namespace.evaluator));
        }
        return metricExpressions;
    }

    /** Loads the time origin of a simulation model. */
    public Instant loadTimeOrigin(eu.cityopt.model.SimulationModel simulationModel) {
        Date timeOriginDate = simulationModel.getTimeorigin();
        return (timeOriginDate != null)
                ? timeOriginDate.toInstant() : DEFAULT_TIME_ORIGIN;
    }

    /**
     * Loads the names and types of named objects in a project: external parameters,
     * input parameters, output variables and metrics.  The actual values are
     * scenario specific, and they are ignored here.
     */
    @Transactional
    public Namespace makeProjectNamespace(int projectId) {
        return makeProjectNamespace(projectRepository.findOne(projectId));
    }

    /**
     * Loads the names and types of named objects in a project: external parameters,
     * decision variables, input parameters, output variables and metrics.  The actual
     * values are ignored here.
     * @param projectId
     * @param scenGenId id of ScenarioGenerator instance from which names and types of
     *   decision variables will be loaded 
     */
    @Transactional
    public Namespace makeProjectNamespace(int projectId, int scenGenId) {
        return makeProjectNamespace(projectRepository.findOne(projectId),
                scenarioGeneratorRepository.findOne(scenGenId));
    }

    /**
     * Loads the names and types of named objects in a project: external parameters,
     * input parameters, output variables and metrics.  The actual values are
     * scenario specific, and they are ignored here.
     */
    public Namespace makeProjectNamespace(Project project) {
        return makeProjectNamespace(project, null);
    }

    /**
     * Loads the names and types of named objects in a project: external parameters,
     * input parameters, output variables and metrics.  The actual values are
     * scenario specific, and they are ignored here.
     * @param project
     * @param scenarioGenerator either null for basic use cases, or ScenarioGenerator
     *    instance from which names and types of decision variables will be loaded 
     */
    public Namespace makeProjectNamespace(Project project, ScenarioGenerator scenarioGenerator) {
        Instant timeOrigin = loadTimeOrigin(project.getSimulationmodel());
        Namespace namespace = new Namespace(evaluator, timeOrigin, (scenarioGenerator != null));
        for (ExtParam mExternal : project.getExtparams()) {
            Type extType = getType(mExternal.getType());
            namespace.externals.put(mExternal.getName(), extType);
        }
        for (Component mComponent : project.getComponents()) {
            Namespace.Component nsComponent = namespace.getOrNew(mComponent.getName());
            for (InputParameter mInput : mComponent.getInputparameters()) {
                Type inputType = getType(mInput.getType());
                nsComponent.inputs.put(mInput.getName(), inputType);
            }
            for (OutputVariable mOutput : mComponent.getOutputvariables()) {
                Type outputType = getType(mOutput.getType());
                nsComponent.outputs.put(mOutput.getName(), outputType);
            }
        }
        for (Metric mMetric : project.getMetrics()) {
            Type metricType = getType(mMetric.getType());
            namespace.metrics.put(mMetric.getName(), metricType);
        }
        if (scenarioGenerator != null) {
            for (DecisionVariable decisionVariable : scenarioGenerator.getDecisionvariables()) {
                InputParameter inputParameter = decisionVariable.getInputparameter();
                if (inputParameter != null) {
                    Type variableType = getType((decisionVariable.getType() != null)
                            ? decisionVariable.getType() : inputParameter.getType());
                    Namespace.Component nsComponent =
                            namespace.components.get(inputParameter.getComponent().getName());
                    nsComponent.decisions.put(inputParameter.getName(), variableType);
                } else {
                    Type variableType = Type.getByName((decisionVariable.getType() != null)
                            ? decisionVariable.getType().getName() : null);
                    namespace.decisions.put(decisionVariable.getName(), variableType);
                }
            }
        }
        return namespace;
    }

    Type getType(eu.cityopt.model.Type type) {
        return Type.getByName((type != null) ? type.getName() : null);
    }

    DbSimulationStorageI makeDbSimulationStorage(int prjid, ExternalParameters externals) {
        return makeDbSimulationStorage(prjid, externals, null, null);
    }

    DbSimulationStorageI makeDbSimulationStorage(
            int prjid, ExternalParameters externals, Integer userId, Integer scenGenId) {
        DbSimulationStorageI storage =
                (DbSimulationStorageI) applicationContext.getBean("dbSimulationStorage");
        storage.initialize(storage, prjid, externals, userId, scenGenId);
        return storage;
    }

    ExtParamValSet saveExternalParameterValues(Project project,
            ExternalParameters simExternals, String setName,
            List<Runnable> idUpdateList) {
        Integer extId = simExternals.getExternalId();
        if (extId != null) {
            // TODO: Should we check if the external parameter value set has changed?
            return extParamValSetRepository.findOne(extId);
        }
        Namespace namespace = simExternals.getNamespace();
    
        ExtParamValSet extParamValSet = new ExtParamValSet();
        extParamValSet.setName(setName);
        for (ExtParam extParam : project.getExtparams()) {
            String extName = extParam.getName();
            Type simType = namespace.externals.get(extName);
            if (simType != null) {
                eu.cityopt.model.Type type = typeRepository.findByNameLike(simType.name);
                ExtParamVal extParamVal = new ExtParamVal();
                extParamVal.setExtparam(extParam);
                if (simType.isTimeSeriesType()) {
                    TimeSeries timeSeries = saveTimeSeries(
                            simExternals.getTS(extName), type,
                            namespace.timeOrigin, idUpdateList);
                    extParamVal.setTimeseries(timeSeries);
                    timeSeries.getExtparamvals().add(extParamVal);
                } else {
                    extParamVal.setValue(simExternals.getString(extName));
                }
                ExtParamValSetComp extParamValSetComp = new ExtParamValSetComp();

                extParamValSetComp.setExtparamval(extParamVal);
                extParamVal.getExtparamvalsetcomps().add(extParamValSetComp);

                extParamValSetComp.setExtparamvalset(extParamValSet);
                extParamValSet.getExtparamvalsetcomps().add(extParamValSetComp);

                extParamValRepository.save(extParamVal);
                extParamValSetCompRepository.save(extParamValSetComp);
                extParamValSetRepository.save(extParamValSet);
            }
        }
        ExtParamValSet finalExtParamValSet = extParamValSetRepository.save(extParamValSet);
        idUpdateList.add(
                () -> simExternals.setExternalId(finalExtParamValSet.getExtparamvalsetid()));
        return finalExtParamValSet;
    }

    TimeSeries saveTimeSeries(TimeSeriesI simTS, eu.cityopt.model.Type type,
            Instant timeOrigin, List<Runnable> idUpdateList) {
        if (simTS.getTimeSeriesId() != null) {
            TimeSeries timeSeries = timeSeriesRepository.findOne(simTS.getTimeSeriesId());
            if (timeSeries != null) {
                //TODO: should we check if the time series has changed?
                return timeSeries;
            }
            simTS.setTimeSeriesId(null);
        }
        TimeSeries timeSeries = new TimeSeries();
        timeSeries.setType(type);
        double[] times = simTS.getTimes();
        double[] values = simTS.getValues();
        int n = times.length;
        for (int i = 0; i < n; ++i) {
            TimeSeriesVal timeSeriesVal = new TimeSeriesVal();

            timeSeriesVal.setTime(TimeUtils.toDate(times[i], timeOrigin));
            timeSeriesVal.setValue(Double.toString(values[i]));

            timeSeriesVal.setTimeseries(timeSeries);
            timeSeries.getTimeseriesvals().add(timeSeriesVal);
        }
        timeSeriesValRepository.save(timeSeries.getTimeseriesvals());
        timeSeriesRepository.save(timeSeries);
        // Copy the database row id once it is available.
        idUpdateList.add(() -> simTS.setTimeSeriesId(timeSeries.getTseriesid()));
        return timeSeries;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Shutting down.");
        jobManager.shutdown();
    }
}
