package eu.cityopt.sim.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValScenMetric;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.ExtParamValRepository;
import eu.cityopt.repository.ExtParamValScenMetricRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.ScenarioMetricsRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.sim.eval.EvaluationException;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InvalidValueException;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulatorConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;

@Service
public class SimulationService {
    class SimulationJob implements Callable<SimulationOutput> {
        int scenId;

        SimulationInput input;
        List<MetricExpression> metricExpressions;

        SimulationModel model;
        SimulationRunner runner;

        SimulationJob(SimulatorManager manager, byte[] modelZipBytes, SimulationInput input,
                Scenario scenario)
                        throws IOException, SimulatorConfigurationException, ScriptException {
            this.scenId = scenario.getScenid();
            this.input = input;
            this.model = manager.parseModel(modelZipBytes);
            this.metricExpressions = loadMetricExpressions(
                    scenario.getProject(), input.getNamespace());
            try {
                this.runner = manager.makeRunner(model, input.getNamespace());
            } catch (Throwable t) {
                model.close();
            }
        }

        @Override
        public SimulationOutput call() throws Exception {
            try {
                Future<SimulationOutput> job = runner.start(input);
                SimulationOutput output = job.get();
                finishSimulation(scenId, output, metricExpressions);
                return output;
            } finally {
                try {
                    runner.close();
                } finally {
                    model.close();
                }
            }
        }
    }

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    SimulationResultRepository simulationResultRepository;

    @Autowired
    MetricValRepository metricValRepository;

    @Autowired
    ScenarioMetricsRepository scenarioMetricsRepository;

    @Autowired
    ExtParamValRepository extParamValRepository;

    @Autowired
    ExtParamValScenMetricRepository extParamValScenMetricRepository;

    @Autowired
    TimeSeriesRepository timeSeriesRepository;

    @Autowired
    TimeSeriesValRepository timeSeriesValRepository;

    @Autowired
    TypeRepository typeRepository;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private PlatformTransactionManager txManager;

    private Evaluator evaluator;

    protected SimulationService() throws EvaluationException, ScriptException {
        evaluator = new Evaluator();
    }

    @Transactional
    public Future<SimulationOutput> startSimulation(int scenId)
            throws ParseException, IOException, SimulatorConfigurationException,
            InterruptedException, ExecutionException, ScriptException {
        Scenario scenario = scenarioRepository.findOne(scenId);
        Callable<SimulationOutput> job = makeSimulationJob(scenario);
        FutureTask<SimulationOutput> futureTask = new FutureTask<SimulationOutput>(job);
        taskExecutor.execute(futureTask);
        return futureTask;
    }

    private void finishSimulation(int scenId, SimulationOutput output,
            Collection<MetricExpression> metricExpressions) throws Exception {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        Exception e = txTemplate.execute(
                (TransactionStatus status) -> {
                    try {
                        Scenario scenario = scenarioRepository.findOne(scenId);
                        if (scenario != null) {
                            saveSimulationResults(scenario, output);
                    
                            if (output instanceof SimulationResults) {
                                SimulationResults results = (SimulationResults) output;
                                MetricValues metricValues = new MetricValues(results, metricExpressions);
                                saveMetricValues(scenario, metricValues);
                            }
                        }
                        return null;
                    } catch (Exception ee) {
                        return ee;
                    }
                });
        if (e != null) {
            throw e;
        }
    }

    public Callable<SimulationOutput> makeSimulationJob(Scenario scenario)
            throws ParseException, IOException, SimulatorConfigurationException,
            InterruptedException, ExecutionException, ScriptException {
        Project project = scenario.getProject();
        Namespace namespace = makeProjectNamespace(project);

        SimulationInput input = loadSimulationInput(scenario, namespace);

        String simulatorName = project.getSimulationmodel().getSimulator();
        SimulatorManager manager = SimulatorManagers.get(simulatorName);
        if (manager == null) {
            throw new SimulatorConfigurationException(
                    "Unknown simulator " + simulatorName);
        }
        byte[] modelZipBytes = project.getSimulationmodel().getModelblob();
        return new SimulationJob(manager, modelZipBytes, input, scenario);
    }

    public ExternalParameters loadExternalParameters(Scenario scenario, Namespace namespace)
            throws ParseException {
        ExternalParameters simExternals = new ExternalParameters(namespace);
        for (ExtParam extParam : scenario.getProject().getExtparams()) {
            Type extType = namespace.externals.get(extParam.getName());
            Object simValue;
            if (extType.isTimeSeriesType()) {
                simValue = loadTimeSeries(extParam.getTimeseries(), extType, namespace.evaluator);
            } else {
                simValue = extType.parse(extParam.getDefaultvalue());
            }
            simExternals.put(extParam.getName(), simValue);
        }
        return simExternals;
    }

    public TimeSeriesI loadTimeSeries(TimeSeries timeseries, 
            Type timeSeriesType, Evaluator evaluator) {
        List<TimeSeriesVal> timeSeriesVals =
                new ArrayList<TimeSeriesVal>(timeseries.getTimeseriesvals());
        timeSeriesVals.sort((e1, e2) -> e1.getTime().compareTo(e2.getTime()));
        int n = timeSeriesVals.size();
        double[] times = new double[n];
        double[] values = new double[n];
        for (int i = 0; i < n; ++i) {
            TimeSeriesVal tsVal = timeSeriesVals.get(i);
            long timeMillis = tsVal.getTime().toInstant().toEpochMilli();
            times[i] = (double) timeMillis / 1000.0;
            values[i] = Double.valueOf(tsVal.getValue());
        }
        return evaluator.makeTS(timeSeriesType, times, values);
    }

    public SimulationInput loadSimulationInput(Scenario scenario, Namespace namespace) 
            throws ParseException {
        ExternalParameters simExternals = loadExternalParameters(scenario, namespace);

        SimulationInput simInput = new SimulationInput(simExternals);
        for (InputParamVal mValue : scenario.getInputparamvals()) {
            InputParameter mInput = mValue.getInputparameter();
            String componentName = mInput.getComponent().getName();
            simInput.putString(componentName, mInput.getName(), mValue.getValue());
        }

        return simInput;
    }

    //TODO loadSimulationOutput, loadMetricValues

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

    public void saveSimulationResults(Scenario scenario, SimulationOutput simOutput) {
        //TODO where to save output.getMessages()
        if (simOutput instanceof SimulationResults) {
            SimulationResults simResults = (SimulationResults) simOutput;
            Set<SimulationResult> newResults = new HashSet<SimulationResult>(); 
            for (Component component : scenario.getProject().getComponents()) {
                String componentName = component.getName();
                for (OutputVariable outputVariable : component.getOutputvariables()) {
                    TimeSeriesI simTS = simResults.getTS(componentName, outputVariable.getName());
                    if (simTS != null) {
                        double[] times = simTS.getTimes();
                        double[] values = simTS.getValues();
                        for (int i = 0; i < times.length; ++i) {
                            SimulationResult newResult = new SimulationResult();
                            newResult.setOutputvariable(outputVariable);
                            newResult.setScenario(scenario);
                            newResult.setTime(new Date((long) (times[i] / 1000.0 + 0.5)));
                            newResult.setValue(Double.toString(values[i]));
                            newResults.add(newResult);
                        }
                    }
                }
            }
            scenario.setSimulationresults(newResults);
            simulationResultRepository.save(newResults);
            scenarioRepository.save(scenario);
        }
    }

    public void saveMetricValues(Scenario scenario, SimulationResults simResults)
            throws ScriptException, InvalidValueException {
        List<MetricExpression> metricExpressions =
                loadMetricExpressions(scenario.getProject(), simResults.getNamespace());
        MetricValues metricValues = new MetricValues(simResults, metricExpressions);
        saveMetricValues(scenario, metricValues);
    }

    public void saveMetricValues(Scenario scenario, MetricValues metricValues) {
        ScenarioMetrics scenarioMetrics = new ScenarioMetrics();

        ExternalParameters simExternals =
                metricValues.getResults().getInput().getExternalParameters();
        saveExternalParameterValues(scenario, simExternals, scenarioMetrics);

        for (Metric metric : scenario.getProject().getMetrics()) {
            String value = null;
            try {
                value = metricValues.getString(metric.getName());
            } catch (IllegalArgumentException e) {
                // Ignore missing values
            }
            if (value != null) {
                MetricVal metricVal = new MetricVal();
                metricVal.setMetric(metric);
                metricVal.setValue(value);
    
                metricVal.setScenariometrics(scenarioMetrics);
                scenarioMetrics.getMetricvals().add(metricVal);
            }
        }

        scenarioMetrics.setScenario(scenario);
        scenario.getScenariometricses().add(scenarioMetrics);

        scenarioRepository.save(scenario);
        scenarioMetricsRepository.save(scenarioMetrics);
        metricValRepository.save(scenarioMetrics.getMetricvals());
    }

    public void saveExternalParameterValues(Scenario scenario,
            ExternalParameters simExternals, ScenarioMetrics newScenarioMetrics) {
        Project project = scenario.getProject();
        Namespace namespace = simExternals.getNamespace();
        for (ExtParam extParam : project.getExtparams()) {
            String extName = extParam.getName();
            Type simType = namespace.externals.get(extName);
            if (simType != null) {
                ExtParamVal extParamVal = new ExtParamVal();
                extParamVal.setExtparam(extParam);
                if (simType.isTimeSeriesType()) {
                    eu.cityopt.model.Type type = findType(simType);
                    TimeSeries timeSeries = saveTimeSeries(simExternals.getTS(extName), type);
                    extParamVal.setTimeseries(timeSeries);
                    timeSeries.getExtparamvals().add(extParamVal);
                } else {
                    extParamVal.setValue(simExternals.getString(extName));
                }
                ExtParamValScenMetric extParamValScenMetric = new ExtParamValScenMetric();

                extParamValScenMetric.setScenariometrics(newScenarioMetrics);
                newScenarioMetrics.getExtparamvalscenmetrics().add(extParamValScenMetric);

                extParamValScenMetric.setExtparamval(extParamVal);
                extParamVal.getExtparamvalscenmetrics().add(extParamValScenMetric);

                extParamValRepository.save(extParamVal);
                extParamValScenMetricRepository.save(extParamValScenMetric);
            }
        }
    }

    public eu.cityopt.model.Type findType(Type simType) {
        for (eu.cityopt.model.Type type : typeRepository.findAll()) {
            if (type.getName().equalsIgnoreCase(simType.name)) {
                return type;
            }
        }
        return null;
    }

    public TimeSeries saveTimeSeries(TimeSeriesI simTS, eu.cityopt.model.Type type) {
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

            long timeMillis = (long)(times[i] * 1000 + 0.5);
            timeSeriesVal.setTime(new Date(timeMillis));
            timeSeriesVal.setValue(Double.toString(values[i]));

            timeSeriesVal.setTimeseries(timeSeries);
            timeSeries.getTimeseriesvals().add(timeSeriesVal);
        }
        timeSeriesValRepository.save(timeSeries.getTimeseriesvals());
        timeSeriesRepository.save(timeSeries);
        //TODO should flush at some point so that the id gets set
        return timeSeries;
    }

    public Namespace makeProjectNamespace(Project project) throws ParseException {
        Namespace namespace = new Namespace(evaluator);
        for (ExtParam mExternal : project.getExtparams()) {
            Type extType = null;
            if (mExternal.getTimeseries() != null) {
                String typeName = mExternal.getTimeseries().getType().getName();
                extType = Type.getByName(typeName);
            } else {
                extType = Type.getFromValue(mExternal.getDefaultvalue());
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
                String typeName = unitRepository.findOne(mOutput.getUnitid()).getType().getName();
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
