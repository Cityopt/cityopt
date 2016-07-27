package eu.cityopt.sim.service;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityNotFoundException;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterators;

import eu.cityopt.model.Component;
import eu.cityopt.model.DecisionVariable;
import eu.cityopt.model.DecisionVariableResult;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.ObjectiveFunctionResult;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptConstraintResult;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenGenResult;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.repository.DecisionVariableResultRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.InputParamValRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.ObjectiveFunctionResultRepository;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.repository.OptConstraintResultRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenGenResultRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.ScenarioMetricsRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.Symbol;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationResults;
import eu.cityopt.sim.opt.Solution;

/**
 * SimulationStorage implementation on top of the data access layer.
 * In other words, the class methods read and write simulation inputs
 * and outputs from the database.
 *
 * @author Hannu Rummukainen
 */
public class DbSimulationStorage implements DbSimulationStorageI {
    private static Logger log = Logger.getLogger(DbSimulationStorage.class);

    private DbSimulationStorageI proxy;
    private int projectId;
    private ExternalParameters externals;
    private Integer userId;
    private Integer scenGenId;

    /**
     * Stored SimulationOutput instances are put inside Gate objects,
     * which are locked to serialize database access to the corresponding
     * Scenario rows in database.
     */
    private class Gate {
        volatile SimulationOutput output;

        Gate(SimulationOutput output) {
            this.output = output;
        }
    }

    private boolean concurrentUpdate = false;

    private volatile boolean cachePopulated = false;
    private Object cacheFillMutex = new Object();
    private ConcurrentMap<SimulationInput, Gate> cache = new ConcurrentHashMap<>();

    @Autowired private SimulationService simulationService;
    @Autowired private SimulationStoreService store;
    @Autowired private ScenarioGenerationService scenarioGenerationService;

    @Autowired private ProjectRepository projectRepository;
    @Autowired private ScenarioRepository scenarioRepository;
    @Autowired private InputParamValRepository inputParamValRepository;
    @Autowired private SimulationResultRepository simulationResultRepository;
    @Autowired private MetricValRepository metricValRepository;
    @Autowired private ScenarioMetricsRepository scenarioMetricsRepository;
    @Autowired private ExtParamValSetRepository extParamValSetRepository;
    @Autowired private TimeSeriesValRepository timeSeriesValRepository;
    @Autowired private ScenarioGeneratorRepository scenarioGeneratorRepository;
    @Autowired private ScenGenResultRepository scenGenResultRepository;
    @Autowired private OptConstraintResultRepository optConstraintResultRepository;
    @Autowired private ObjectiveFunctionResultRepository objectiveFunctionResultRepository;
    @Autowired private DecisionVariableResultRepository decisionVariableResultRepository;
    @Autowired private OptConstraintRepository optConstraintRepository;
    @Autowired private ObjectiveFunctionRepository objectiveFunctionRepository;

    @Override
    public void initialize(
            DbSimulationStorageI proxy,
            int projectId, ExternalParameters externals,
            Integer userId, Integer scenGenId) {
        this.proxy = proxy;
        this.projectId = projectId;
        this.externals = externals;
        this.userId = userId;
        this.scenGenId = scenGenId;
    }

    //TODO: maybe use entity listener to get updates?

    @Override
    public Iterator<SimulationOutput> iterator() {
        if ( ! cachePopulated) {
            proxy.loadCache();
        }
        return Iterators.transform(cache.values().iterator(), gate -> gate.output);
    }

    @Override
    public SimulationOutput get(SimulationInput input) {
        if ( ! cachePopulated) {
            proxy.loadCache();
        }
        Gate gate = cache.get(input);
        return (gate != null) ? gate.output : null;
    }

    @Override
    public void put(SimulationStorage.Put put) {
        Gate gate = cache.computeIfAbsent(put.input, k -> new Gate(put.output));
        synchronized (concurrentUpdate ? gate : cache) {
            if (gate.output != put.output && gate.output != null) {
                Integer oldScenId = gate.output.getInput().getScenarioId();
                if (oldScenId != null && put.input.getScenarioId() == null) {
                    put.input.setScenarioId(oldScenId);
                }
            }
            gate.output = put.output;

        	try {
        		proxy.doPutTransaction(put);
        	} catch (Throwable t) {
        		log.error("FAILED " + contextMessage(put), t);
        		throw t;
        	}
        }
    }

    String contextMessage(SimulationStorage.Put put) {
    	return "Put scenario " + put.name + " (" + put.description + ")";
    }

    @Override
    @Transactional
    public void doPutTransaction(SimulationStorage.Put put) {
    	String context = contextMessage(put);
    	log.debug("Entering " + context);
    	try {
	        Scenario scenario = saveSimulationInput(put.input, put.name, put.description);
	        boolean newScenario = (put.input.getScenarioId() == null);
	        if (put.output != null) {
	            saveSimulationOutput(scenario, put.output);
	        }
	        if (put.metricValues != null) {
	            scenario = saveMetricValues(scenario, newScenario, put.metricValues);
	        }
	        if (scenGenId != null) {
	            saveGeneratorScenarioData(
	                    scenario, put.decisions, put.constraintStatus, put.objectiveStatus);
	        }
	        scenarioRepository.flush();
	        put.input.setScenarioId(scenario.getScenid());
    	} catch (Throwable t) {
    		log.debug("Failing " + context, t);
    		throw t;
    	}
		log.debug("Exiting " + context);
    }

    @Override
    @Transactional
    public void updateMetricValues(MetricValues metricValues) {
        Scenario scenario = getScenario(metricValues.getResults().getInput());
        if (scenario != null) {
            saveMetricValues(scenario, false, metricValues);
        }
    }

    @Override
    @Transactional
    public void loadCache() {
        synchronized (cacheFillMutex) {
            if ( ! cachePopulated) {
                Project project = getProject();
                for (Scenario scenario : project.getScenarios()) {
                    loadScenarioToCache(scenario);
                }
                cachePopulated = true;
            }
        }
    }

    private void loadScenarioToCache(Scenario scenario) {
        try {
            SimulationInput input =
                    simulationService.loadSimulationInput(scenario, externals);
            SimulationOutput output =
                    simulationService.loadSimulationOutput(scenario, input);
            Gate oldGate = cache.putIfAbsent(input, new Gate(output));
            if (oldGate != null) {
                synchronized (oldGate) {
                    oldGate.output = output;
                }
            }
        } catch (ParseException e) {
            log.warn("Cannot load simulation input and output from scenario "
                    + scenario.getScenid() + ": " + e.getMessage());
        }
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
        if (scenId == null) {
            // Check if there is a copy of the input already out there.
            if ( ! cachePopulated) {
                loadCache();
            }
            Gate gate = cache.get(input);
            SimulationOutput output = (gate != null) ? gate.output : null;
            if (output != null && output.getInput() != input) {
                scenId = output.getInput().getScenarioId();
                input.setScenarioId(scenId);
            }
        }
        if (scenId != null) {
            Scenario scenario = scenarioRepository.findOne(scenId);
            if (scenario != null && scenario.getProject() != null
                    && scenario.getProject().getPrjid() == projectId) {
                return scenario;
            }
            log.warn("Scenario " + scenId + " has been removed from the database.");
            input.setScenarioId(null);
        }
        return null;
    }

    private ScenarioGenerator getScenarioGenerator() {
        if (scenGenId != null) {
            return scenarioGeneratorRepository.findOne(scenGenId);
        } else {
            return null;
        }
    }

    String getFreshScenarioName(String basename) {
        if (basename == null) {
            basename = "Generated at " + Instant.now();
        }
        int i = 1;
        String name = basename;
        while (scenarioRepository.findByNamePrjid(name, projectId) != null) {
        	name = basename + "." + i;
        	++i;
        }
        return name;
    }

    Scenario saveSimulationInput(
            SimulationInput simInput, String scenarioName, String scenarioDescription) {
        Scenario scenario = getScenario(simInput);
        if (scenario != null) {
            //TODO might want to write anyway, in case someone changed the database
            return scenario;
        }
        scenarioName = getFreshScenarioName(scenarioName);
        Project project = getProject();
        Date now = new Date();
        scenario = new Scenario();
        scenario.setName(scenarioName);
        scenario.setDescription(scenarioDescription);
        scenario.setCreatedby(userId);
        scenario.setCreatedon(now);
        ScenarioGenerator scenGen = getScenarioGenerator();
        if (scenGen != null) {
            scenario.setScenariogenerator(scenGen);
            scenGen.getScenarios().add(scenario);
        }
        scenario.setStatus(null);
        scenario.setProject(project);
        scenario = scenarioRepository.save(scenario);
        project.getScenarios().add(scenario);
        List<Runnable> idUpdates = new ArrayList<Runnable>();

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
                        if (simType.isTimeSeriesType()) {
                            TimeSeriesI simTS = simInput.getTS(componentName, inputName);
                            if (simTS != null) {
                                TimeSeries timeSeries = store.saveTimeSeries(
                                        simTS, simType, namespace.timeOrigin);
                                inputParamVal.setTimeseries(timeSeries);
                            }
                        } else {
                            inputParamVal.setValue(simInput.getString(componentName, inputName));
                        }
                        inputParamVal.setInputparameter(inputParameter);
                        inputParameter.getInputparamvals().add(inputParamVal);

                        inputParamVal.setScenario(scenario);
                        scenario.getInputparamvals().add(inputParamVal);
                    }
                }
            }
            timeSeriesValRepository.flush();
            for (Runnable update : idUpdates) {
                update.run();
            }
        }
        inputParamValRepository.save(scenario.getInputparamvals());

        return scenario;
    }

    Scenario saveSimulationOutput(Scenario scenario, SimulationOutput simOutput) {
        scenario.setLog(simOutput.getMessages());
        scenario.setRunstart((simOutput.runStart != null) ? Date.from(simOutput.runStart) : null);
        scenario.setRunend((simOutput.runEnd != null) ? Date.from(simOutput.runEnd) : null);

        scenarioMetricsRepository.delete(scenario.getScenariometricses());
        scenario.getScenariometricses().clear();
        List<SimulationResult> oldResults =
                simulationResultRepository.findByScenId(scenario.getScenid());
        simulationResultRepository.delete(oldResults);
        simulationResultRepository.flush();

        if (simOutput instanceof SimulationResults) {
            scenario.setStatus(SimulationService.STATUS_SUCCESS);
            SimulationResults simResults = (SimulationResults) simOutput;
            Namespace namespace = simResults.getNamespace();
            List<SimulationResult> newResults = new ArrayList<SimulationResult>();
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
                                TimeSeries timeSeries = store.saveTimeSeries(
                                        simTS, simType, namespace.timeOrigin);
                                timeSeriesValRepository.flush();
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
            scenario.setUpdatedby(userId);
            scenario.setUpdatedon(new Date());
            simulationResultRepository.save(newResults);
            scenario = scenarioRepository.save(scenario);

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
            scenario.setUpdatedby(userId);
            scenario.setUpdatedon(new Date());
            scenario = scenarioRepository.save(scenario);
        }
        return scenario;
    }

    Scenario saveMetricValues(Scenario scenario, boolean newScenario, MetricValues metricValues) {
        ExternalParameters simExternals =
                metricValues.getResults().getInput().getExternalParameters();
        List<Runnable> idUpdateList = new ArrayList<Runnable>();
        ExtParamValSet extParamValSet = simulationService.saveExternalParameterValues(
                scenario.getProject(), simExternals, null, idUpdateList);

        if (!newScenario) {
            ScenarioMetrics oldScenarioMetrics =
                    scenarioMetricsRepository.findByScenidAndExtParamValSetid(
                            scenario.getScenid(),
                            (extParamValSet != null) ? extParamValSet.getExtparamvalsetid() : null);
            if (oldScenarioMetrics != null) {
                scenarioMetricsRepository.delete(oldScenarioMetrics);
            }
            scenarioMetricsRepository.flush();
        }
        ScenarioMetrics scenarioMetrics = new ScenarioMetrics();
        // Link to Scenario and ExtParamValSet in one direction only, JPA will do the rest.
        scenarioMetrics.setScenario(scenario);
        scenarioMetrics.setExtparamvalset(extParamValSet);

        Namespace namespace = metricValues.getResults().getNamespace();
        for (Metric metric : scenario.getProject().getMetrics()) {
            Object value = metricValues.get(metric.getName());
            if (value != null) {
                MetricVal metricVal = new MetricVal();
                metricVal.setMetric(metric);
                Type simType = namespace.metrics.get(metric.getName());
                if (simType.isTimeSeriesType()) {
                    TimeSeriesI simTS = (TimeSeriesI) value;
                    TimeSeries timeSeries = store.saveTimeSeries(
                            simTS, simType, namespace.timeOrigin);
                    metricVal.setTimeseries(timeSeries);
                } else {
                    String text = simType.format(value, namespace);
                    metricVal.setValue(text);
                }
                metricVal.setScenariometrics(scenarioMetrics);
                scenarioMetrics.getMetricvals().add(metricVal);
            } // else: ignore missing values
        }

        scenarioMetricsRepository.save(scenarioMetrics);
        metricValRepository.save(scenarioMetrics.getMetricvals());

        timeSeriesValRepository.flush();
        extParamValSetRepository.flush();
        for (Runnable update : idUpdateList) {
            update.run();
        }
        return scenario;
    }

    public void saveGeneratorScenarioData(
            Scenario scenario, DecisionValues decisionValues,
            ConstraintStatus constraintStatus, ObjectiveStatus objectiveStatus) {
        ScenGenResult scenGenResult = null;
        ScenarioGenerator scenarioGenerator = null;
        for (ScenGenResult sgr : scenario.getScengenresults()) {
            if (sgr.getScenariogenerator().getScengenid() == scenGenId) {
                scenGenResult = sgr;
                scenarioGenerator = sgr.getScenariogenerator();
            }
        }
        if (scenGenResult == null) {
            scenarioGenerator = scenarioGeneratorRepository.findOne(scenGenId);
            scenGenResult = new ScenGenResult();
            scenGenResult.setScenario(scenario);

            scenGenResult.setScenariogenerator(scenarioGenerator);
            scenarioGenerator.getScengenresults().add(scenGenResult);
        }

        scenGenResult.setParetooptimal(null);
        scenGenResult.setFeasible(
                (constraintStatus != null) ? constraintStatus.feasible : null);
        scenGenResult = scenGenResultRepository.save(scenGenResult);

        if (decisionValues != null) {
            Map<Integer, DecisionVariableResult> oldDecisionVariableResults = new HashMap<>();
            for (DecisionVariableResult dvr : scenGenResult.getDecisionvariableresults()) {
                oldDecisionVariableResults.put(dvr.getDecisionvariable().getDecisionvarid(), dvr);
            }
            for (DecisionVariable decisionVariable : scenarioGenerator.getDecisionvariables()) {
                DecisionVariableResult decisionVariableResult =
                        oldDecisionVariableResults.remove(decisionVariable.getDecisionvarid());
                if (decisionVariableResult == null) {
                    decisionVariableResult = new DecisionVariableResult();
                    decisionVariableResult.setDecisionvariable(decisionVariable);
                    decisionVariableResult.setScengenresult(scenGenResult);
                    scenGenResult.getDecisionvariableresults().add(decisionVariableResult);
                }
                Symbol symbol = scenarioGenerationService.getDecisionVariableSymbol(decisionVariable);
                String value = decisionValues.getString(symbol.componentName, symbol.name);
                decisionVariableResult.setValue(value);
            }
            decisionVariableResultRepository.save(scenGenResult.getDecisionvariableresults());
            decisionVariableResultRepository.delete(oldDecisionVariableResults.values());
        }

        if (constraintStatus != null) {
            Map<Integer, OptConstraintResult> oldOptConstraintResults = new HashMap<>();
            for (OptConstraintResult ocr : scenGenResult.getOptconstraintresults()) {
                oldOptConstraintResults.put(ocr.getOptconstraint().getOptconstid(), ocr);
            }
            int i = 0;
            for (Constraint constraint : constraintStatus.constraints) {
                OptConstraintResult optConstraintResult =
                        oldOptConstraintResults.remove(constraint.getConstraintId());
                if (optConstraintResult == null) {
                    optConstraintResult = new OptConstraintResult();
                    OptConstraint optConstraint =
                            optConstraintRepository.findOne(constraint.getConstraintId());
                    optConstraintResult.setOptconstraint(optConstraint);
                    optConstraintResult.setScengenresult(scenGenResult);
                    scenGenResult.getOptconstraintresults().add(optConstraintResult);
                }
                optConstraintResult.setInfeasibility(
                        Double.toString(constraintStatus.infeasibilities[i]));
                ++i;
            }
            optConstraintResultRepository.save(scenGenResult.getOptconstraintresults());
            optConstraintResultRepository.delete(oldOptConstraintResults.values());
        }

        if (objectiveStatus != null) {
            Map<Integer, ObjectiveFunctionResult> oldObjectiveFunctionResults = new HashMap<>();
            for (ObjectiveFunctionResult ofr : scenGenResult.getObjectivefunctionresults()) {
                oldObjectiveFunctionResults.put(ofr.getObjectivefunction().getObtfunctionid(), ofr);
            }
            int i = 0;
            for (ObjectiveExpression objective : objectiveStatus.objectives) {
                ObjectiveFunctionResult objectiveFunctionResult =
                        oldObjectiveFunctionResults.remove(objective.getObjectiveId());
                if (objectiveFunctionResult == null) {
                    objectiveFunctionResult = new ObjectiveFunctionResult();
                    ObjectiveFunction objectiveFunction =
                            objectiveFunctionRepository.findOne(objective.getObjectiveId());
                    objectiveFunctionResult.setObjectivefunction(objectiveFunction);
                    objectiveFunctionResult.setScengenresult(scenGenResult);
                    scenGenResult.getObjectivefunctionresults().add(objectiveFunctionResult);
                }
                objectiveFunctionResult.setValue(
                        Double.toString(objectiveStatus.objectiveValues[i]));
                ++i;
            }
            objectiveFunctionResultRepository.save(scenGenResult.getObjectivefunctionresults());
            objectiveFunctionResultRepository.delete(oldObjectiveFunctionResults.values());
        }
    }

    @Override
    @Transactional
    public void saveScenarioGeneratorResults(
            OptimisationResults results, String messages) {
        ScenarioGenerator scenarioGenerator = scenarioGeneratorRepository.findOne(scenGenId);
        if (scenarioGenerator != null) {
            scenarioGenerator.setStatus(results.toString());
            scenarioGenerator.setLog(messages);
            if (results.paretoFront != null) {
                saveParetoFront(scenarioGenerator, results.paretoFront);
            }
            scenarioGeneratorRepository.save(scenarioGenerator);
        } else {
            Log.warn("Failed to save status: ScenarioGenerator " + scenGenId + " has been deleted");
        }
    }

    public void saveParetoFront(
            ScenarioGenerator scenarioGenerator, Collection<Solution> paretoFront) {
        Set<Integer> dominantScenarioIds = new HashSet<>();
        for (Solution solution : paretoFront) {
            Scenario scenario = getScenario(solution.input);
            if (scenario != null) {
                dominantScenarioIds.add(scenario.getScenid());
            }
        }
        for (ScenGenResult scenGenResult : scenarioGenerator.getScengenresults()) {
            int scenarioId = scenGenResult.getScenario().getScenid();
            boolean paretoOptimal = dominantScenarioIds.contains(scenarioId);
            scenGenResult.setParetooptimal(paretoOptimal);
        }
        scenGenResultRepository.save(scenarioGenerator.getScengenresults());
    }
}
