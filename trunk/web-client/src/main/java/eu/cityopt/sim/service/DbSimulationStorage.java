package eu.cityopt.sim.service;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityNotFoundException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.ExtParamValRepository;
import eu.cityopt.repository.ExtParamValSetCompRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.InputParamValRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.ScenarioMetricsRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.eval.util.TimeUtils;

public class DbSimulationStorage implements DbSimulationStorageI {
    private static Logger log = Logger.getLogger(DbSimulationStorage.class); 

    private int projectId;
    private ExternalParameters externals;
    private Integer userId;
    private Integer scenGenId;

    private boolean cachePopulated = false;
    private ConcurrentMap<SimulationInput, SimulationOutput> cache
        = new ConcurrentHashMap<SimulationInput, SimulationOutput>();

    @Autowired private SimulationService simulationService;

    @Autowired private ProjectRepository projectRepository;
    @Autowired private ScenarioRepository scenarioRepository;
    @Autowired private InputParamValRepository inputParamValRepository;
    @Autowired private SimulationResultRepository simulationResultRepository;
    @Autowired private MetricValRepository metricValRepository;
    @Autowired private ScenarioMetricsRepository scenarioMetricsRepository;
    @Autowired private ExtParamValRepository extParamValRepository;
    @Autowired private ExtParamValSetRepository extParamValSetRepository;
    @Autowired private ExtParamValSetCompRepository extParamValSetCompRepository;
    @Autowired private TimeSeriesRepository timeSeriesRepository;
    @Autowired private TimeSeriesValRepository timeSeriesValRepository;
    @Autowired private TypeRepository typeRepository;
    @Autowired private ScenarioGeneratorRepository scenarioGeneratorRepository;

    @Override
    public void initialize(int projectId, ExternalParameters externals,
            Integer userId, Integer scenGenId) {
        this.projectId = projectId;
        this.externals = externals;
        this.userId = userId;
        this.scenGenId = scenGenId;
    }

    //TODO: maybe use entity listener to get updates?

    @Override
    public SimulationOutput get(SimulationInput input) {
        if ( ! cachePopulated) {
            loadCache();
        }
        return cache.get(input);
    }

    @Override
    @Transactional
    public void put(SimulationOutput output) {
        put(output, null, null);
    }

    @Override
    @Transactional
    public void put(SimulationOutput output, String scenarioName, String scenarioDescription) {
        cache.put(output.getInput(), output);
        Scenario scenario = saveSimulationInput(output.getInput(), scenarioName, scenarioDescription);
        saveSimulationOutput(scenario, output);
    }

    @Override
    @Transactional
    public void updateMetricValues(MetricValues metricValues) {
        Scenario scenario = getScenario(metricValues.getResults().getInput());
        if (scenario != null) {
            saveMetricValues(scenario, metricValues);
        }
    }

    private void loadCache() {
        Project project = getProject();
        for (Scenario scenario : project.getScenarios()) {
            try {
                SimulationInput input = simulationService.loadSimulationInput(scenario, externals);
                SimulationOutput output = simulationService.loadSimulationOutput(scenario, input);
                cache.put(input, output);
            } catch (ParseException e) {
                log.warn("Cannot load simulation input and output from scenario "
                        + scenario.getScenid() + ": " + e.getMessage());
            }
        }
        cachePopulated = true;
    }

    private Project getProject() {
        Project project = projectRepository.findOne(projectId);
        if (project == null) {
            cache.clear();
            String msg = "Project " + projectId + " has been removed from the database."; 
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
        return project;
    }

    private Scenario getScenario(SimulationInput input) {
        Integer scenId = input.getScenarioId();
        if (scenId != null) {
            Scenario scenario = scenarioRepository.findOne(scenId);
            if (scenario.getProject().getPrjid() == projectId) {
                return scenario;
            }
            log.warn("Scenario " + scenId + " has been removed from the database.");
            input.setScenarioId(null);
        }
        return null;
    }

    private ScenarioGenerator getScenarioGenerator() {
        return scenarioGeneratorRepository.findOne(scenGenId);
    }

    Scenario saveSimulationInput(
            SimulationInput simInput, String scenarioName, String scenarioDescription) {
        Scenario scenario = getScenario(simInput);
        if (scenario != null) {
            //TODO might want to write anyway, in case someone changed the database
            return scenario;
        }
        if (scenarioName == null) {
            scenarioName = "Generated at " + Instant.now();
        }
        Project project = getProject();
        Date now = new Date();
        scenario = new Scenario();
        scenario.setName(scenarioName);
        scenario.setDescription(scenarioDescription);
        scenario.setCreatedby(userId);
        scenario.setCreatedon(now);
        scenario.setScenariogenerator(getScenarioGenerator());
        scenario.setStatus(null);

        Namespace namespace = simInput.getNamespace();
        for (Component component : project.getComponents()) {
            String componentName = component.getName();
            Namespace.Component nsComponent = namespace.components.get(componentName);
            if (nsComponent != null) {
                for (InputParameter inputParameter : component.getInputparameters()) {
                    String inputName = inputParameter.getName();
                    Type simType = nsComponent.inputs.get(inputName);
                    if (simType != null) {
                        InputParamVal inputParamVal = new InputParamVal();
                        inputParamVal.setCreatedby(userId);
                        inputParamVal.setCreatedon(now);
                        inputParamVal.setValue(simInput.getString(componentName, inputName));

                        inputParamVal.setInputparameter(inputParameter);
                        inputParameter.getInputparamvals().add(inputParamVal);
                    }
                }
            }
        }
        inputParamValRepository.save(scenario.getInputparamvals());

        scenario.setProject(project);
        project.getScenarios().add(scenario);
        return scenarioRepository.save(scenario);
    }

    void saveSimulationOutput(Scenario scenario, SimulationOutput simOutput) {
        scenario.setLog(simOutput.getMessages());
        scenario.setRunstart((simOutput.runStart != null) ? Date.from(simOutput.runStart) : null);
        scenario.setRunend((simOutput.runEnd != null) ? Date.from(simOutput.runEnd) : null);

        for (ScenarioMetrics scenarioMetrics : scenario.getScenariometricses()) {
            metricValRepository.delete(scenarioMetrics.getMetricvals());
        }
        scenarioMetricsRepository.delete(scenario.getScenariometricses());
        scenario.getScenariometricses().clear();
        //TODO batch removal of simulation results and related time series
        for (Component component : scenario.getProject().getComponents()) {
            for (OutputVariable outputVariable : component.getOutputvariables()) {
                SimulationResult oldResult =
                        simulationResultRepository.findByScenAndOutvar(
                                scenario.getScenid(), outputVariable.getOutvarid());
                if (oldResult != null) {
                    outputVariable.getSimulationresults().remove(oldResult);
                    simulationResultRepository.delete(oldResult);
                    log.debug("Deleting old simulationResult " + oldResult);
                }
            }
        }
        simulationResultRepository.flush();

        if (simOutput instanceof SimulationResults) {
            scenario.setStatus(SimulationService.STATUS_SUCCESS);
            SimulationResults simResults = (SimulationResults) simOutput;
            Namespace namespace = simResults.getNamespace();
            Set<SimulationResult> newResults = new HashSet<SimulationResult>(); 
            List<Runnable> idUpdates = new ArrayList<Runnable>();
            for (Component component : scenario.getProject().getComponents()) {
                String componentName = component.getName();
                Namespace.Component nsComponent = namespace.components.get(componentName);
                if (nsComponent != null) {
                    for (OutputVariable outputVariable : component.getOutputvariables()) {
                        String outputName = outputVariable.getName();
                        Type simType = nsComponent.outputs.get(outputName);
                        if (simType != null) {
                            TimeSeriesI simTS = simResults.getTS(componentName, outputName);
                            if (simTS != null) {
                                eu.cityopt.model.Type type = findType(simType);
                                TimeSeries timeSeries =
                                        saveTimeSeries(simTS, type, namespace.timeOrigin, idUpdates);

                                SimulationResult simulationResult = new SimulationResult();

                                simulationResult.setScenario(scenario);
                                newResults.add(simulationResult);

                                simulationResult.setTimeseries(timeSeries);

                                simulationResult.setOutputvariable(outputVariable);
                                outputVariable.getSimulationresults().add(simulationResult);

                                simulationResultRepository.save(simulationResult);
                            }
                        }
                    }
                }
            }
            scenario.setSimulationresults(newResults);
            simulationResultRepository.save(newResults);
            scenarioRepository.save(scenario);

            timeSeriesValRepository.flush();
            for (Runnable update : idUpdates) {
                update.run();
            }
        } else {
            SimulationFailure simFailure = (SimulationFailure) simOutput;
            if (simFailure.permanent) {
                scenario.setStatus(SimulationService.STATUS_MODEL_FAILURE);
            } else {
                scenario.setStatus(SimulationService.STATUS_SIMULATOR_FAILURE);
            }
            scenarioRepository.save(scenario);
        }
    }

    void saveMetricValues(Scenario scenario, MetricValues metricValues) {
        ScenarioMetrics scenarioMetrics = new ScenarioMetrics();

        ExternalParameters simExternals =
                metricValues.getResults().getInput().getExternalParameters();
        List<Runnable> idUpdateList = new ArrayList<Runnable>();
        saveExternalParameterValues(scenario, simExternals, scenarioMetrics, idUpdateList);

        for (Metric metric : scenario.getProject().getMetrics()) {
            String value = null;
            try {
                value = metricValues.getString(metric.getName());
            } catch (IllegalArgumentException e) {
                // Ignore missing values
            }
            //TODO time series
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

        timeSeriesValRepository.flush();
        extParamValSetRepository.flush();
        for (Runnable update : idUpdateList) {
            update.run();
        }
    }

    void saveExternalParameterValues(Scenario scenario,
            ExternalParameters simExternals, ScenarioMetrics newScenarioMetrics,
            List<Runnable> idUpdateList) {
        Integer extId = simExternals.getExternalId();
        if (extId != null) {
            // TODO: Should we check if the external parameter value set has changed?
            return;
        }
        Project project = scenario.getProject();
        Namespace namespace = simExternals.getNamespace();

        ExtParamValSet extParamValSet = new ExtParamValSet();
        for (ExtParam extParam : project.getExtparams()) {
            String extName = extParam.getName();
            Type simType = namespace.externals.get(extName);
            if (simType != null) {
                ExtParamVal extParamVal = new ExtParamVal();
                extParamVal.setExtparam(extParam);
                if (simType.isTimeSeriesType()) {
                    eu.cityopt.model.Type type = findType(simType);
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

        newScenarioMetrics.setExtparamvalset(extParamValSet);
        extParamValSet.getScenariometricses().add(newScenarioMetrics);

        extParamValSetRepository.save(extParamValSet);
        idUpdateList.add(
                () -> simExternals.setExternalId(extParamValSet.getExtparamvalsetid()));
    }

    eu.cityopt.model.Type findType(Type simType) {
        for (eu.cityopt.model.Type type : typeRepository.findAll()) {
            if (type.getName().equalsIgnoreCase(simType.name)) {
                return type;
            }
        }
        return null;
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
}
