package eu.cityopt.sim.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.script.ScriptException;

import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Algorithm;
import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.model.Unit;
import eu.cityopt.opt.io.CsvTimeSeriesData;
import eu.cityopt.opt.io.OptimisationProblemIO;
import eu.cityopt.opt.io.TimeSeriesData;
import eu.cityopt.repository.AlgorithmRepository;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ExtParamRepository;
import eu.cityopt.repository.InputParameterRepository;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.OutputVariableRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.AlgorithmParameters;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.SimulationStructure;

/**
 * Importing and exporting data between the database and specially formatted CSV
 * and property files.
 * 
 * @author Hannu Rummukainen
 */
@Named
@Singleton
public class ImportExportService {
    public static final String KEY_ALGORITHM_NAME = "algorithm";

    @Inject private SimulationService simulationService;
    @Inject private ScenarioGenerationService scenarioGenerationService;

    @Inject private ProjectRepository projectRepository;
    @Inject private AlgorithmRepository algorithmRepository;
    @Inject private ScenarioGeneratorRepository scenarioGeneratorRepository;
    @Inject private ExtParamRepository extParamRepository;
    @Inject private ComponentRepository componentRepository;
    @Inject private InputParameterRepository inputParameterRepository;
    @Inject private OutputVariableRepository outputVariableRepository;
    @Inject private MetricRepository metricRepository;
    @Inject private UnitRepository unitRepository;

    /**
     * Creates external parameters, input parameters, output variables
     * and/or metrics from text files.  As the supported text format
     * does not include units (only types), the method uses arbitrary
     * units of the correct type.
     * 
     * @param projectId the associate project.  Any existing external
     *   parameters, input parameters, output variables and metrics
     *   will be overwritten in case of name clashes, and otherwise left
     *   untouched.
     * @param structureFile path to file defining some or all of external
     *   parameters, input parameters, output variables and/or metrics.
     *   The file format is compatible with the optimisation problem format
     *   of {@link #importOptimisationProblem(int, String, Path, Integer, Path, Path...)};
     *   any decision variables, objectives and constraints are ignored.
     * @throws ParseException
     * @throws IOException
     * @throws ScriptException
     */
    @Transactional
    public void importSimulationStructure(int projectId, Path structureFile)
            throws IOException, ParseException, ScriptException {
        Project project = projectRepository.findOne(projectId);

        // Note: We use a dummy timeOrigin because there is no time series data
        // here, and the constructed Namespace is discarded immediately.
        EvaluationSetup setup =
                new EvaluationSetup(simulationService.getEvaluator(), Instant.EPOCH);
        SimulationStructure structure =
                OptimisationProblemIO.readStructureCsv(structureFile, setup);

        saveSimulationStructure(project, structure);
    }

    public void saveSimulationStructure(
            Project project, SimulationStructure structure) {
        Map<Type, Unit> unitMap = pickUnits();
        saveExternalParameters(project, structure.getNamespace(), unitMap);
        saveNamespaceComponents(project, structure.getNamespace(), unitMap);
        saveMetrics(project, structure.getNamespace(), structure.metrics, unitMap);
        projectRepository.save(project);
    }

    Map<Type, Unit> pickUnits() {
        Map<Type, Unit> unitMap = new HashMap<>();
        for (Unit unit : unitRepository.findAll()) {
            Type type = Type.getByName(unit.getType().getName());
            Unit oldUnit = unitMap.get(type);
            // We prefer units with shorter names. As long as there are no units
            // in the CSV files, we should really prefer dimensionless units here.
            if (oldUnit == null || unit.getName().length() < oldUnit.getName().length()) {
                unitMap.put(type, unit);
            }
        }
        return unitMap;
    }

    public void saveExternalParameters(
            Project project, Namespace namespace, Map<Type, Unit> unitMap) {
        Map<String, ExtParam> old = new HashMap<>();
        for (ExtParam extParam : project.getExtparams()) {
            old.put(extParam.getName(), extParam);
        }
        List<ExtParam> changed = new ArrayList<>();
        for (Map.Entry<String, Type> entry : namespace.externals.entrySet()) {
            String name = entry.getKey();
            Type type = entry.getValue();

            // If there is an old ExtParam of different type, delete it.
            ExtParam extParam = old.get(name);
            if (extParam != null) {
                eu.cityopt.model.Type oldType = extParam.getUnit().getType();
                if ( ! oldType.getName().equalsIgnoreCase(type.name)) {
                    extParamRepository.delete(extParam);
                    extParam = null;
                }
            }
            // Add a new ExtParam if necessary.
            if (extParam == null) {
                extParam = new ExtParam();
                extParam.setName(entry.getKey());
                extParam.setUnit(unitMap.get(entry.getValue()));
                extParam.setProject(project);
                project.getExtparams().add(extParam);
                changed.add(extParam);
            }
        }
        extParamRepository.save(changed);
    }

    public void saveNamespaceComponents(
            Project project, Namespace namespace, Map<Type, Unit> unitMap) {
        Map<String, Component> oldComponents = new HashMap<>();
        for (Component component : project.getComponents()) {
            oldComponents.put(component.getName(), component);
        }
        for (Map.Entry<String, Namespace.Component> entry : namespace.components.entrySet()) {
            String componentName = entry.getKey();
            Namespace.Component nsComponent = entry.getValue();

            Component component = oldComponents.get(componentName);
            if (component == null) {
                component = new Component();
                component.setName(componentName);
                component.setProject(project);
                project.getComponents().add(component);
                componentRepository.save(component);
            }

            saveComponentInputParameters(component, nsComponent.inputs, unitMap);
            saveComponentOutputVariables(component, nsComponent.outputs, unitMap);
        }
    }

    public void saveComponentInputParameters(
            Component component, Map<String, Type> inputs, Map<Type, Unit> unitMap) {
        List<InputParameter> changedInputParameters = new ArrayList<>();
        Map<String, InputParameter> oldInputParameters = new HashMap<>();
        for (InputParameter inputParameter : component.getInputparameters()) {
            oldInputParameters.put(inputParameter.getName(), inputParameter);
        }
        for (Map.Entry<String, Type> entry : inputs.entrySet()) {
            String name = entry.getKey();
            Type type = entry.getValue();
            
            // If there is an old InputParameter of different type, delete it.
            InputParameter inputParameter = oldInputParameters.get(name);
            if (inputParameter != null) {
                eu.cityopt.model.Type oldType = inputParameter.getUnit().getType();
                if ( ! oldType.getName().equalsIgnoreCase(type.name)) {
                    inputParameterRepository.delete(inputParameter);
                    inputParameter = null;
                }
            }
            // Add a new InputParameter if necessary.
            if (inputParameter == null) {
                inputParameter = new InputParameter();
                inputParameter.setName(name);
                inputParameter.setUnit(unitMap.get(type));
                inputParameter.setComponent(component);
                component.getInputparameters().add(inputParameter);
                changedInputParameters.add(inputParameter);
            }
        }
        inputParameterRepository.save(changedInputParameters);
    }

    public void saveComponentOutputVariables(
            Component component, Map<String, Type> outputs, Map<Type, Unit> unitMap) {
        Map<String, OutputVariable> oldOutputVariables = new HashMap<>();
        for (OutputVariable outputVariable : component.getOutputvariables()) {
            oldOutputVariables.put(outputVariable.getName(), outputVariable);
        }
        List<OutputVariable> changedOutputVariables = new ArrayList<>();
        for (Map.Entry<String, Type> entry : outputs.entrySet()) {
            String name = entry.getKey();
            Type type = entry.getValue();

            // If there is an old OutputVariable of different type, delete it.
            OutputVariable outputVariable = oldOutputVariables.get(name);
            if (outputVariable != null) {
                eu.cityopt.model.Type oldType = outputVariable.getUnit().getType();
                if ( ! oldType.getName().equalsIgnoreCase(type.name)) {
                    outputVariableRepository.delete(outputVariable);
                    outputVariable = null;
                }
            }
            // Add a new OutputVariable if necessary.
            if (outputVariable == null) {
                outputVariable = new OutputVariable();
                outputVariable.setName(name);
                outputVariable.setUnit(unitMap.get(type));
                outputVariable.setComponent(component);
                component.getOutputvariables().add(outputVariable);
                changedOutputVariables.add(outputVariable);
            }
        }
        outputVariableRepository.save(changedOutputVariables);
    }

    public void saveMetrics(Project project, Namespace namespace,
            Collection<MetricExpression> metricExpressions, Map<Type, Unit> unitMap) {
        Map<String, Metric> oldMetrics = new HashMap<>();
        for (Metric metric : project.getMetrics()) {
            oldMetrics.put(metric.getName(), metric);
        }
        Map<String, MetricExpression> expressionMap = new HashMap<>();
        for (MetricExpression metricExpression : metricExpressions) {
            expressionMap.put(metricExpression.getMetricName(), metricExpression);
        }
        List<Metric> changedMetrics = new ArrayList<>();
        for (Map.Entry<String, Type> entry : namespace.metrics.entrySet()) {
            String name = entry.getKey();
            Type type = entry.getValue();
            String expression = expressionMap.get(name).getSource();

            // If there is an old Metric of different type, or with a different
            // expression, delete it.
            Metric metric = oldMetrics.get(name);
            if (metric != null) {
                eu.cityopt.model.Type oldType = metric.getUnit().getType();
                String oldExpression = metric.getExpression();
                if ( ! (oldType.getName().equalsIgnoreCase(type.name)
                        && oldExpression.equals(expression))) {
                    metricRepository.delete(metric);
                    metric = null;
                }
            }
            // Add a new Metric if necessary.
            if (metric == null) {
                metric = new Metric();
                metric.setName(name);
                metric.setUnit(unitMap.get(type));
                metric.setExpression(expressionMap.get(name).getSource());
                metric.setProject(project);
                project.getMetrics().add(metric);
                changedMetrics.add(metric);
            }
        }
        metricRepository.save(changedMetrics);
    }

    /** 
     * Creates a new ScenarioGenerator row from text files.
     * @param projectId the associated project, which must have exactly the
     *   same external parameters, input parameters, output variables and
     *   metrics as the optimisation problem.  In an empty project they can
     *   be set up by calling {@link #importSimulationStructure(int, Path, Path...)}
     *   with the same input files.
     *   The project must also have a SimulationModel with a defined time origin
     *   in the database.
     * @param name name for the ScenarioGenerator row
     * @param problemFile defines the objectives and constraints
     * @param algorithmId identifies the optimisation algorithm.  May be left
     *   null, in which case the algorithm can be set in algorithm parameters.
     * @param algorithmParameterFile algorithm parameters. May be left null.
     * @param timeSeriesFiles paths of CSV files containing time series data
     *   for external parameters
     * @return id of created ScenarioGenerator row
     * @throws IOException 
     * @throws ConfigurationException 
     * @throws ParseException 
     * @throws ScriptException 
     */
    @Transactional
    public int importOptimisationProblem(
            int projectId, String name, Path problemFile, Integer algorithmId,
            Path algorithmParameterFile, Path... timeSeriesFiles) 
                    throws IOException, ConfigurationException, ParseException, ScriptException {
        Project project = projectRepository.findOne(projectId);

        AlgorithmParameters algorithmParameters = null;
        if (algorithmParameterFile != null) {
            algorithmParameters = readAlgorithmParameters(algorithmParameterFile);
        }

        Algorithm algorithm = null;
        if (algorithmId != null) {
            algorithm = algorithmRepository.findOne(algorithmId);
        } else if (algorithmParameters != null) {
            algorithm = findAlgorithm(algorithmParameters);
        }

        TimeSeriesData timeSeriesData =
                readTimeSeriesCsv(project, timeSeriesFiles);
        OptimisationProblem problem =
                OptimisationProblemIO.readProblemCsv(problemFile, timeSeriesData);

        return saveOptimisationProblem(project, name, problem, algorithm, algorithmParameters);
    }

    public int saveOptimisationProblem(
            Project project, String name, OptimisationProblem problem, 
            Algorithm algorithm, AlgorithmParameters algorithmParameters) {
        ScenarioGenerator scenarioGenerator = new ScenarioGenerator();
        scenarioGenerator.setName(name);
        scenarioGenerator.setAlgorithm(algorithm);
        scenarioGenerator.setProject(project);
        project.getScenariogenerators().add(scenarioGenerator);
        scenarioGenerator = scenarioGeneratorRepository.save(scenarioGenerator);

        if (algorithmParameters != null) {
            scenarioGenerationService.saveAlgorithmParameters(
                    scenarioGenerator, algorithm, algorithmParameters);
        }

        scenarioGenerationService.saveOptimisationProblem(problem, scenarioGenerator);
        scenarioGeneratorRepository.flush();
        return scenarioGenerator.getScengenid();
    }

    /**
     * Reads time series data from CSV files.
     * @param project the time origin is read from the project's
     *   simulation model data
     * @param csvFiles paths to CSV files containing time series data.
     *   See {@link CsvTimeSeriesData} for the required contents.
     */
    public TimeSeriesData readTimeSeriesCsv(Project project, Path... csvFiles)
            throws IOException, ParseException {
        CsvTimeSeriesData tsd = makeTimeSeriesReader(project);
        for (Path path : csvFiles) {
            tsd.read(path);
        }
        return tsd;
    }

    /**
     * Constructs an object for reading time series data from CSV files.
     * @param project the time origin is read from the project's
     *   simulation model data
     */
    public CsvTimeSeriesData makeTimeSeriesReader(Project project) {
        Instant timeOrigin = project.getSimulationmodel().getTimeorigin().toInstant();
        EvaluationSetup setup = new EvaluationSetup(simulationService.getEvaluator(), timeOrigin);
        return new CsvTimeSeriesData(setup);
    }

    /**
     * Reads algorithm parameters from a properties file.
     * @throws IOException 
     */
    public AlgorithmParameters readAlgorithmParameters(Path path) throws IOException {
        AlgorithmParameters ap = new AlgorithmParameters();
        try (InputStream stream = new FileInputStream(path.toFile())) {
            ap.load(stream);
        }
        return ap;
    }

    /** Returns the Algorithm named in parameters, or null if not set. */
    public Algorithm findAlgorithm(AlgorithmParameters algorithmParameters)
            throws ConfigurationException {
        String name = algorithmParameters.getString(KEY_ALGORITHM_NAME, null);
        if (name != null) {
            for (Algorithm algorithm : algorithmRepository.findAll()) {
                if (algorithm.getDescription().equalsIgnoreCase(name)) {
                    return algorithm;
                }
            }
            throw new ConfigurationException("Unknown algorithm " + name);
        }
        return null;
    }
}
