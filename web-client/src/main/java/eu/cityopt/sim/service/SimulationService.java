package eu.cityopt.sim.service;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.sim.eval.EvaluationException;
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
import eu.cityopt.sim.eval.SimulatorConfigurationException;
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
 * We use an injected TaskExecutor to perform computations and store results in
 * the background. The actual simulations are run in separate processes.
 * 
 * @author Hannu Rummukainen
 */
@Service
public class SimulationService {
    class SimulationJob implements Callable<SimulationOutput> {
        private final int scenId;
        private final SimulationInput input;
        private final List<MetricExpression> metricExpressions;
        private final SimulationModel model;
        private final SimulationRunner runner;
        private final SimulationStorage storage;

        volatile boolean cancelled;
        volatile Future<SimulationOutput> activeJob;

        SimulationJob(int scenId, SimulationInput input, List<MetricExpression> metricExpressions,
                SimulationModel model, SimulationRunner runner, SimulationStorage storage) {
            this.scenId = scenId;
            this.input = input;
            this.metricExpressions = metricExpressions;
            this.model = model;
            this.runner = runner;
            this.storage = storage;
        }

        @Override
        public SimulationOutput call() throws Exception {
            try {
                if (cancelled) {
                    return null;
                }
                Instant runStart = Instant.now();
                Future<SimulationOutput> job = runner.start(input);

                // Write to activeJob happens-before read from cancelled.
                activeJob = job;
                if (cancelled) {
                    job.cancel(true);
                    return null;
                }

                // Wait for completion or cancellation.
                SimulationOutput output = job.get();
                output.runStart = runStart;
                output.runEnd = Instant.now();

                // Write to activeJob happens-before read from cancelled.
                activeJob = null;
                if (cancelled) {
                    return null;
                }

                storage.put(output);
                if (output instanceof SimulationResults) {
                    SimulationResults results = (SimulationResults) output;
                    MetricValues metricValues = new MetricValues(results, metricExpressions);
                    storage.updateMetricValues(metricValues);
                }
                return output;
            } finally {
                activeJobs.remove(scenId, this);
                try {
                    runner.close();
                } finally {
                    model.close();
                }
            }
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            if (!cancelled) {
                // Write to cancelled happens-before read from activeJob.
                cancelled = true;
                Future<SimulationOutput> job = activeJob;
                if (job != null) {
                    job.cancel(mayInterruptIfRunning);
                }
                return true;
            }
            return false;
        }
    }

    private static final Instant DEFAULT_TIME_ORIGIN = Instant.ofEpochMilli(0);

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_MODEL_FAILURE = "MODEL_FAILURE";
    public static final String STATUS_SIMULATOR_FAILURE = "SIMULATOR_FAILURE";

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TaskExecutor taskExecutor;

    private Evaluator evaluator;

    private ConcurrentHashMap<Integer, SimulationJob> activeJobs
        = new ConcurrentHashMap<Integer, SimulationJob>();

    protected SimulationService() throws EvaluationException, ScriptException {
        evaluator = new Evaluator();
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
            throws ParseException, IOException, SimulatorConfigurationException,
            InterruptedException, ExecutionException, ScriptException {
        Scenario scenario = scenarioRepository.findOne(scenId);
        SimulationJob job = makeSimulationJob(scenario);
        FutureTask<SimulationOutput> futureTask = new FutureTask<SimulationOutput>(job) {
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean cancelled = job.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return cancelled;
            }
        };
        taskExecutor.execute(futureTask);
        return futureTask;
    }

    /**
     * Returns set of scenarios for which a simulation is currently running.
     * The result is a snapshot of the situation at the time the method is called.
     * @return set of scenario ids
     */
    public Set<Integer> getRunningScenarios() {
        return new HashSet<Integer>(activeJobs.keySet());
    }

    /**
     * Cancels an ongoing simulation.
     * @param scenId scenario id
     * @return true if the simulation was cancelled.  Returns false if there is
     *  no simulation to cancel, or the simulation has already completed, or
     *  the cancellation fails for some other reason.
     */
    public boolean cancelSimulation(int scenId) {
        SimulationJob oldJob = activeJobs.remove(scenId);
        if (oldJob != null) {
            return oldJob.cancel(true);
        }
        return false;
    }

    SimulationJob makeSimulationJob(Scenario scenario)
            throws ParseException, IOException, SimulatorConfigurationException,
            InterruptedException, ExecutionException, ScriptException {
        Project project = scenario.getProject();
        Namespace namespace = makeProjectNamespace(project);

        ExternalParameters externals = loadExternalParametersFromDefaults(scenario, namespace);
        SimulationInput input = loadSimulationInput(scenario, externals);

        String simulatorName = project.getSimulationmodel().getSimulator();
        SimulatorManager manager = SimulatorManagers.get(simulatorName);
        if (manager == null) {
            throw new SimulatorConfigurationException(
                    "Unknown simulator " + simulatorName);
        }
        byte[] modelZipBytes = project.getSimulationmodel().getModelblob();
        SimulationModel model = manager.parseModel(modelZipBytes);
        List<MetricExpression> metricExpressions = loadMetricExpressions(project, namespace);
        SimulationRunner runner = null;
        try {
            runner = manager.makeRunner(model, input.getNamespace());
            DbSimulationStorageI storage =
                    (DbSimulationStorageI) applicationContext.getBean("dbSimulationStorage");
            storage.initialize(project.getPrjid(), externals, null, null);
            SimulationJob newJob = new SimulationJob(
                    scenario.getScenid(), input, metricExpressions, model, runner, storage);
            SimulationJob oldJob = activeJobs.put(scenario.getScenid(), newJob);
            if (oldJob != null) {
                oldJob.cancel(true);
            }
            return newJob;
        } finally {
            if (runner == null) model.close();
        }
    }

    /** Loads the default values of external parameters. */
    public ExternalParameters loadExternalParametersFromDefaults(
            Scenario scenario, Namespace namespace) throws ParseException {
        ExternalParameters simExternals = new ExternalParameters(namespace);
        for (ExtParam extParam : scenario.getProject().getExtparams()) {
            Type extType = namespace.externals.get(extParam.getName());
            Object simValue;
            if (extType.isTimeSeriesType()) {
                simValue = loadTimeSeries(extParam.getTimeseries(), extType,
                        namespace.evaluator, namespace.timeOrigin);
            } else {
                simValue = extType.parse(extParam.getDefaultvalue(), namespace);
            }
            simExternals.put(extParam.getName(), simValue);
        }
        return simExternals;
    }

    /** Loads the data of a time series. */
    public TimeSeriesI loadTimeSeries(TimeSeries timeseries, 
            Type timeSeriesType, Evaluator evaluator, Instant timeOrigin) {
        List<TimeSeriesVal> timeSeriesVals =
                new ArrayList<TimeSeriesVal>(timeseries.getTimeseriesvals());
        timeSeriesVals.sort((e1, e2) -> e1.getTime().compareTo(e2.getTime()));
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

    /** Loads the simulation result data of a scenario. */
    public SimulationOutput loadSimulationOutput(Scenario scenario, SimulationInput simInput)
            throws ParseException {
        if (scenario.getStatus() == STATUS_SUCCESS) {
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
                return simResults;
            } else {
                return new SimulationFailure(simInput, false,
                        "Could not find simulation results for all output variables in database.");
            }
        } else {
            boolean permanent = (scenario.getStatus() == STATUS_MODEL_FAILURE);
            return new SimulationFailure(simInput, permanent, scenario.getLog());
        }
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

    /**
     * Loads the names and types of named objects in a project: external parameters,
     * input parameters, output variables and metrics.  The actual values are
     * scenario specific, and they are ignored here.
     */
    public Namespace makeProjectNamespace(Project project) throws ParseException {
        Date timeOriginDate = project.getSimulationmodel().getTimeorigin();
        Instant timeOrigin = (timeOriginDate != null)
                ? timeOriginDate.toInstant() : DEFAULT_TIME_ORIGIN;
        Namespace namespace = new Namespace(evaluator, timeOrigin);
        for (ExtParam mExternal : project.getExtparams()) {
            Type extType = null;
            if (mExternal.getTimeseries() != null) {
                String typeName = mExternal.getTimeseries().getType().getName();
                extType = Type.getByName(typeName);
            } else {
                String typeName = mExternal.getUnit().getType().getName();
                extType = Type.getByName(typeName);
            }
            namespace.externals.put(mExternal.getName(), extType);
        }
        for (Component mComponent : project.getComponents()) {
            Namespace.Component nsComponent = namespace.getOrNew(mComponent.getName());
            for (InputParameter mInput : mComponent.getInputparameters()) {
                String typeName = mInput.getUnit().getType().getName();
                Type inputType = Type.getByName(typeName);
                nsComponent.inputs.put(mInput.getName(), inputType);
            }
            for (OutputVariable mOutput : mComponent.getOutputvariables()) {
                String typeName = mOutput.getUnit().getType().getName();
                Type outputType = Type.getByName(typeName);
                nsComponent.outputs.put(mOutput.getName(), outputType);
            }
        }
        for (Metric mMetric : project.getMetrics()) {
            String typeName = mMetric.getUnit().getType().getName();
            Type metricType = Type.getByName(typeName);
            namespace.metrics.put(mMetric.getName(), metricType);
        }
        return namespace;
    }
}
