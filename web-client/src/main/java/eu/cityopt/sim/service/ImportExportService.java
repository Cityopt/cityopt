package eu.cityopt.sim.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.script.ScriptException;

import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Algorithm;
import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.model.Unit;
import eu.cityopt.opt.io.CsvTimeSeriesData;
import eu.cityopt.opt.io.ExportBuilder;
import eu.cityopt.opt.io.JacksonBinder;
import eu.cityopt.opt.io.JacksonBinderScenario;
import eu.cityopt.opt.io.JacksonBinderScenario.ScenarioItem;
import eu.cityopt.opt.io.OptimisationProblemIO;
import eu.cityopt.opt.io.TimeSeriesData;
import eu.cityopt.opt.io.TimeSeriesData.Series;
import eu.cityopt.repository.AlgorithmRepository;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ExtParamRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.InputParameterRepository;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.OutputVariableRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationModelRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.SimulatorManagers;
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

    /** When importing a model, this is the default simulated period. */
    static final long DEFAULT_SIMULATED_TIME_SECONDS = TimeUnit.DAYS.toSeconds(365); 

    @Inject private SimulationService simulationService;
    @Inject private ScenarioGenerationService scenarioGenerationService;
    @Inject private OptimisationSupport optimisationSupport;

    @Inject private ProjectRepository projectRepository;
    @Inject private SimulationModelRepository simulationModelRepository;
    @Inject private AlgorithmRepository algorithmRepository;
    @Inject private ScenarioGeneratorRepository scenarioGeneratorRepository;
    @Inject private ExtParamRepository extParamRepository;
    @Inject private ComponentRepository componentRepository;
    @Inject private InputParameterRepository inputParameterRepository;
    @Inject private OutputVariableRepository outputVariableRepository;
    @Inject private MetricRepository metricRepository;
    @Inject private TypeRepository typeRepository;
    @Inject private OptimizationSetRepository optimizationSetRepository;
    @Inject private ExtParamValSetRepository extParamValSetRepository;
    @Inject private ScenarioRepository scenarioRepository;
    @Inject private UnitRepository unitRepository;

    /**
     * Creates a SimulationModel row in the database.
     * The imageblob field of the SimulationModel is left null.
     * 
     * @param projectId id of the project in which the model is initially inserted,
     *    or null if the model is not inserted in any project.
     * @param userId id of the creating user, or null
     * @param description model description
     * @param modelData the binary model data (e.g. zip file bytes)
     * @param simulatorName a simulator name from {@link SimulatorManagers}.
     *    If null, reading the model will be attempted with support code for
     *    different simulators, and the first to succeed wins.
     * @param overrideTimeOrigin the time corresponding to a simulation time of zero.
     *   If null, then an attempt is made to get the origin from the model data.
     * @return id of the created SimulationModel row
     * @throws ConfigurationException
     * @throws IOException
     */
    @Transactional
    public int importSimulationModel(Integer projectId, Integer userId,
            String description, byte[] modelData,
            String simulatorName, Instant overrideTimeOrigin)
                    throws ConfigurationException, IOException {
        try (SimulationModel model = SimulatorManagers.parseModel(simulatorName, modelData)) {
            Instant timeOrigin = (overrideTimeOrigin != null)
                    ? overrideTimeOrigin : model.getTimeOrigin();
            if (timeOrigin == null) {
                throw new ConfigurationException("No time origin provided");
            }
    
            eu.cityopt.model.SimulationModel simulationModel =
                    new eu.cityopt.model.SimulationModel();
            simulationModel.setCreatedby(userId);
            simulationModel.setCreatedon(new Date());
            simulationModel.setDescription(description);
            simulationModel.setModelblob(modelData);
            simulationModel.setSimulator(model.getSimulatorName());
            simulationModel.setTimeorigin(Date.from(timeOrigin));
            simulationModel = simulationModelRepository.save(simulationModel);
    
            if (projectId != null) {
                Project project = projectRepository.findOne(projectId);
                project.setSimulationmodel(simulationModel);
                simulationModel.getProjects().add(project);
                projectRepository.save(project);
            }
            return simulationModel.getModelid();
        }
    }

    /**
     * Creates input parameters and output variables from model data.
     * The required components are also created.  The types of the
     * inputs and outputs are set, but the units are left null.
     * The artificial configuration component CITYOPT is also created
     * if it does not already exist.  The default values are read
     * from the model as far as possible.  The default simulation
     * period is 365 days starting from the model time origin.
     *
     * @param projectId id of the project to be modified.  The project
     *   must already have a SimulationModel with a set time origin.
     *   Any existing input parameters and output variables will be
     *   overwritten in case of name clashes, and otherwise left untouched.
     * @param detailLevel indicates how much of the available input
     *   parameters and output variables are to be included.  0 is minimal,
     *   larger numbers may provide more results.
     * @return human-readable warning messages about possible problems,
     *   e.g. if there are invalid component or variable names.
     * @throws ConfigurationException
     * @throws IOException
     */
    @Transactional
    public String importModelInputsAndOutputs(
            int projectId, int detailLevel) throws ConfigurationException, IOException {
        Project project = projectRepository.findOne(projectId);
        try (SimulationModel model = simulationService.loadSimulationModel(project)) {
            Instant timeOrigin = simulationService.loadTimeOrigin(project.getSimulationmodel());
            Namespace namespace = new Namespace(simulationService.getEvaluator(), timeOrigin);
            namespace.initConfigComponent();
            Map<String, Map<String, String>> units = new HashMap<>();
            StringWriter warnings = new StringWriter();
            SimulationInput defaultInput = model.findInputsAndOutputs(
                    namespace, units, detailLevel, warnings);

            defaultInput.put(Namespace.CONFIG_COMPONENT, Namespace.CONFIG_SIMULATION_START, 0.0);
            defaultInput.put(Namespace.CONFIG_COMPONENT, Namespace.CONFIG_SIMULATION_END,
                    (double) DEFAULT_SIMULATED_TIME_SECONDS);

            saveNamespaceComponents(project, namespace, units);
            projectRepository.save(project);
            saveDefaultInput(project, defaultInput);
            return warnings.toString();
        }
    }

    /**
     * Creates external parameters, input parameters, output variables
     * and/or metrics from text files.  As the supported text format
     * does not include units for now (only types), the method leaves
     * units null.
     * 
     * @param projectId the associate project.  Any existing external
     *   parameters, input parameters, output variables and metrics
     *   will be overwritten in case of name clashes, and otherwise left
     *   untouched.
     * @param structureStream CSV file defining some or all of external
     *   parameters, input parameters, output variables and/or metrics.
     *   The file format is compatible with the optimisation problem format
     *   of {@link #importOptimisationProblem(int, String, Path, Integer, Path, Path...)};
     *   any decision variables, objectives and constraints are ignored.
     * @throws ParseException
     * @throws IOException
     * @throws ScriptException
     */
    @Transactional
    public void importSimulationStructure(int projectId, InputStream structureStream)
            throws IOException, ParseException, ScriptException {
        Project project = projectRepository.findOne(projectId);

        // Note: We use a dummy timeOrigin because there is no time series data
        // here, and the constructed Namespace is discarded immediately.
        EvaluationSetup setup =
                new EvaluationSetup(simulationService.getEvaluator(), Instant.EPOCH);
        SimulationStructure structure =
                OptimisationProblemIO.readStructureCsv(structureStream, setup);

        saveSimulationStructure(project, structure);
    }
    
    /**
     * Export external parameters, inputs, outputs and metrics.
     * This is the inverse of importSimulationStructure.
     * @param projectId project to export
     * @param out output stream.
     * @throws ScriptException 
     * @throws IOException 
     */
    @Transactional(readOnly=true)
    public void exportSimulationStructure(int projectId, OutputStream out)
            throws ScriptException, IOException {
        Project project = projectRepository.findOne(projectId);
        Namespace ns = simulationService.makeProjectNamespace(project);
        SimulationStructure sim = new SimulationStructure(null, ns);
        sim.metrics = simulationService.loadMetricExpressions(project, ns);
        OptimisationProblemIO.writeStructureCsv(sim, out);
    }
    

    public void saveSimulationStructure(
            Project project, SimulationStructure structure) {
        saveExternalParameters(project, structure.getNamespace());
        saveNamespaceComponents(project, structure.getNamespace(), null);
        saveMetrics(project, structure.getNamespace(), structure.metrics);
        projectRepository.save(project);
    }

    public void saveExternalParameters(Project project, Namespace namespace) {
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
                eu.cityopt.model.Type oldType = extParam.getType();
                if ( ! oldType.getName().equalsIgnoreCase(type.name)) {
                    extParamRepository.delete(extParam);
                    extParam = null;
                }
            }
            // Add a new ExtParam if necessary.
            if (extParam == null) {
                extParam = new ExtParam();
                extParam.setName(entry.getKey());
                extParam.setProject(project);
                extParam.setType(typeRepository.findByNameLike(type.name));
                project.getExtparams().add(extParam);
                changed.add(extParam);
            }
        }
        extParamRepository.save(changed);
    }

    public void saveNamespaceComponents(Project project, Namespace namespace,
    		Map<String, Map<String, String>> units) {
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

            Map<String, String> compUnits = (units != null) ? units.get(componentName) : null;
            saveComponentInputParameters(component, nsComponent.inputs, compUnits);
            saveComponentOutputVariables(component, nsComponent.outputs, compUnits);
        }
    }

    public void saveComponentInputParameters(Component component, Map<String, Type> inputs,
    		Map<String, String> units) {
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
                eu.cityopt.model.Type oldType = inputParameter.getType();
                if ( ! oldType.getName().equalsIgnoreCase(type.name)) {
                    inputParameterRepository.delete(inputParameter);
                    inputParameter = null;
                }
            }
            // Add a new InputParameter if necessary.
            if (inputParameter == null) {
                inputParameter = new InputParameter();
                inputParameter.setName(name);
                inputParameter.setComponent(component);
                inputParameter.setType(typeRepository.findByNameLike(type.name));
                inputParameter.setUnit(findOrCreateUnit(units, name));
                component.getInputparameters().add(inputParameter);
                changedInputParameters.add(inputParameter);
            }
        }
        inputParameterRepository.save(changedInputParameters);
    }

    public void saveComponentOutputVariables(Component component, Map<String, Type> outputs,
    		Map<String, String> units) {
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
                eu.cityopt.model.Type oldType = outputVariable.getType();
                if ( ! oldType.getName().equalsIgnoreCase(type.name)) {
                    outputVariableRepository.delete(outputVariable);
                    outputVariable = null;
                }
            }
            // Add a new OutputVariable if necessary.
            if (outputVariable == null) {
                outputVariable = new OutputVariable();
                outputVariable.setName(name);
                outputVariable.setComponent(component);
                outputVariable.setType(typeRepository.findByNameLike(type.name));
                outputVariable.setUnit(findOrCreateUnit(units, name));
                component.getOutputvariables().add(outputVariable);
                changedOutputVariables.add(outputVariable);
            }
        }
        outputVariableRepository.save(changedOutputVariables);
    }

    private Unit findOrCreateUnit(Map<String, String> units, String key) {
    	if (units != null && key != null) {
    		String unitName = units.get(key);
    		if (unitName != null) {
    			unitName = unitName.trim();
	    		if (!unitName.isEmpty()) {
		    		Unit unit = unitRepository.findByName(unitName);
		    		if (unit != null) {
		    			return unit;
		    		} else {
			    		unit = new Unit();
			    		unit.setName(unitName);
			    		return unitRepository.save(unit);
		    		}
	    		}
    		}
    	}
		return null;
	}

	public void saveMetrics(Project project, Namespace namespace,
            Collection<MetricExpression> metricExpressions) {
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
                eu.cityopt.model.Type oldType = metric.getType();
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
                metric.setExpression(expressionMap.get(name).getSource());
                metric.setProject(project);
                metric.setType(typeRepository.findByNameLike(type.name));
                project.getMetrics().add(metric);
                changedMetrics.add(metric);
            }
        }
        metricRepository.save(changedMetrics);
    }

    void saveDefaultInput(Project project, SimulationInput defaultInput) {
        Namespace namespace = defaultInput.getNamespace();
        List<InputParameter> changedInputs = new ArrayList<>();
        for (Component component : project.getComponents()) {
            for (InputParameter inputParameter : component.getInputparameters()) {
                Type type = namespace.getInputType(component.getName(), inputParameter.getName());
                if (type != null) {
                    Object value = defaultInput.get(component.getName(), inputParameter.getName());
                    if (value != null) {
                        String text = type.format(value, namespace);
                        inputParameter.setDefaultvalue(text);
                        changedInputs.add(inputParameter);
                    }
                }
            }
        }
        inputParameterRepository.save(changedInputs);
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
     * @param name name for the ScenarioGenerator row.  The same name is used for
     *   the created ExtParamValSet.
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
    
    @Transactional(readOnly=true)
    public void exportOptimisationProblem(
            int sgid, Path problemFile, Path timeSeriesFile)
                    throws ScriptException, ParseException,
                            ConfigurationException, IOException {
        ScenarioGenerator sg = scenarioGeneratorRepository.findOne(sgid);
        OptimisationProblem
                p = scenarioGenerationService.loadOptimisationProblem(
                        sg.getProject(), sg);
        OptimisationProblemIO.writeProblemCsv(p, problemFile, timeSeriesFile);
        //TODO algorithm and parameters?
    }

    /** 
     * Creates a new OptimizationSet row from text files.
     * The file format is the same as supported by importOptimizationProblem,
     * but any decision variables and input values/expressions are ignored. 
     * If there are multiple objectives in the problem file, only one of them
     * is stored.
     * 
     * @param projectId the associated project, which must have exactly the
     *   same external parameters, input parameters, output variables and
     *   metrics as the optimisation problem.  In an empty project they can
     *   be set up by calling {@link #importSimulationStructure(int, Path, Path...)}
     *   with the same input files.
     *   The project must also have a SimulationModel with a defined time origin
     *   in the database.
     * @param name name for the OptimizationSet row.  The same name is used for
     *   the created ExtParamValSet.
     * @param problemFile defines the objective and constraints
     * @param timeSeriesFiles paths of CSV files containing time series data
     *   for external parameters
     * @return id of created OptimizationSet row
     * @throws IOException 
     * @throws ParseException 
     * @throws ScriptException 
     */
    @Transactional
    public int importOptimisationSet(
            int projectId, Integer userId,
            String name, Path problemFile, Path... timeSeriesFiles) 
                   throws IOException, ParseException, ScriptException
    {
        Project project = projectRepository.findOne(projectId);

        TimeSeriesData timeSeriesData =
                readTimeSeriesCsv(project, timeSeriesFiles);
        //TODO should have a specific method for reading an optimization set
        OptimisationProblem problem =
                OptimisationProblemIO.readProblemCsv(problemFile, timeSeriesData);

        return optimisationSupport.saveOptimisationSet(project, userId, name, problem);
    }
    
    @Transactional(readOnly=true)
    public void exportOptimisationSet(
            int optSetId, Path problemFile, Path timeSeriesFile)
                    throws ParseException, ScriptException, IOException {
        OptimizationSet os = optimizationSetRepository.findOne(optSetId);
        Project proj = os.getProject();
        OptimisationProblem prob = optimisationSupport.loadOptimisationProblem(
                proj, os);
        OptimisationProblemIO.writeProblemCsv(
                prob, problemFile, timeSeriesFile);
    }

    /**
	 * Imports external parameter values, input parameter values and simulation
	 * results for scenarios of a project. Existing scenarios are replaced when
	 * their name matches, and otherwise kept unchanged.  The project default
	 * external parameter value set is replaced with some external parameter value
	 * set from the input; if there is more than one then one is chosen at random.
	 * <p>
	 * External parameter values are optional.  For each scenario, there may be
	 * only input parameter values, or both input parameter values and simulation
	 * results, or neither.
	 * <p>
	 * Metric values in the input are ignored, and instead new metric values for
	 * the scenarios are computed with current metric expressions and default
	 * external parameter value set (whichever set is picked).
	 * 
	 * @param projectId
	 * @param scenarioFile
	 * @param timeSeriesFiles
	 * @throws ParseException
	 */
    @Transactional
    public void importScenarioData(int projectId, Path scenarioFile, Path... timeSeriesFiles)
    		throws IOException, ParseException {
        Project project = projectRepository.findOne(projectId);
		CsvTimeSeriesData tsd = makeTimeSeriesReader(project);
        for (Path tsPath : timeSeriesFiles) {
        	tsd.read(tsPath);
        }
        List<JacksonBinderScenario.ScenarioItem> items = OptimisationProblemIO.readMulti(scenarioFile);
        Map<String, List<JacksonBinder.ExtParam>> epsEXT = new HashMap<>();
        Map<String, List<JacksonBinder.Input>> scenIN = new HashMap<>(); 
        Map<String, List<JacksonBinder.Output>> scenOUT = new HashMap<>();
        for (ScenarioItem si : items) {
        	switch (si.getKind()) {
        	case EXT:
        		epsEXT.computeIfAbsent(si.extparamvalsetname, k -> new ArrayList<>())
        		.add((JacksonBinder.ExtParam) si.getItem());
        		break;
        	case IN: {
        		JacksonBinder.Input input = (JacksonBinder.Input) si.getItem();
        		scenIN.computeIfAbsent(si.scenarioname, k -> new ArrayList<>())
        		.add(input);
        		break;
        	}
        	case OUT: {
        		JacksonBinder.Output output = (JacksonBinder.Output) si.getItem();
        		scenOUT.computeIfAbsent(si.scenarioname, k -> new ArrayList<>())
        		.add(output);
        	}
    		default: //Ignore
        	}
        }

        Namespace ns = simulationService.makeProjectNamespace(projectId);
        List<MetricExpression> metricExpressions = null;
        try {
        	metricExpressions = simulationService.loadMetricExpressions(project, ns);
        } catch (ScriptException e) { /* ignore */ }

        Map<String, ExternalParameters> externals = new HashMap<>();
        List<Runnable> idUpdateList = new ArrayList<>();
        ExternalParameters defaultExternals = new ExternalParameters(ns);
        ExtParamValSet defaultEPS = null;
        for (Map.Entry<String, List<JacksonBinder.ExtParam>> entry : epsEXT.entrySet()) {
        	ExternalParameters externalData = new ExternalParameters(ns);
        	String setName = entry.getKey();
        	for (JacksonBinder.ExtParam ep : entry.getValue()) {
        		Type type = ns.externals.get(ep.name);
        		if (type != null) {
	        		if (type.isTimeSeriesType()) {
	        			Series series = tsd.getSeries(ep.tsKey());
	        			if (series != null) {
	            			externalData.put(ep.name,
	            					ns.evaluator.makeTS(type, series.getTimes(), series.getValues()));
	        			}
	        		} else {
	            		externalData.putString(ep.name, ep.value);
	        		}
        		}
        	}
        	externals.put(setName, externalData);
        	defaultEPS = simulationService.saveExternalParameterValues(
        			project, externalData, setName, idUpdateList);
	    	project = projectRepository.findOne(projectId);
        	defaultExternals = externalData;
        }
        if (defaultEPS != null) {
	        // XXX the default ExtParamValSet is set randomly if there are multiple
	    	// ExtParamValSets in the data.
	    	project.setDefaultextparamvalset(defaultEPS);
	    	project = projectRepository.save(project);
        }

        Map<String, SimulationInput> inputs = new HashMap<>();
        for (Map.Entry<String, List<JacksonBinder.Input>> entry : scenIN.entrySet()) {
        	String scenName = entry.getKey();
        	if (scenName != null) {
        		SimulationInput inputData = new SimulationInput(defaultExternals);
        		for (JacksonBinder.Input in : entry.getValue()) {
            		Type type = ns.getInputType(in.comp, in.name);
            		if (type != null) {
            			inputData.putString(in.comp, in.name, in.value);
            		}
        		}
        		inputs.put(scenName, inputData);
        	}
        }

        Map<String, SimulationResults> results = new HashMap<>();
        for (Map.Entry<String, List<JacksonBinder.Output>> entry : scenOUT.entrySet()) {
        	String scenName = entry.getKey();
    		SimulationInput inputData = inputs.get(scenName);
    		if (inputData != null) {
    			SimulationResults resultData = new SimulationResults(inputData, "");
    			for (JacksonBinder.Output out : entry.getValue()) {
            		Type type = ns.getOutputType(out.comp, out.name);
            		if (type != null) {
		        		if (type.isTimeSeriesType()) {
		        			Series series = tsd.getSeries(out.tsKey());
		        			if (series != null) {
		            			resultData.put(out.comp, out.name,
		            					ns.evaluator.makeTS(type, series.getTimes(), series.getValues()));
		        			}
		        		} else {
		        			resultData.putString(out.comp, out.name, out.value);
		        		}
            		}
    			}
    			results.put(scenName, resultData);
    		}
        }

        String description = "Imported from " + scenarioFile.getFileName().toString();
        SimulationStorage storage = simulationService.makeDbSimulationStorage(
        		projectId, defaultExternals);
        Set<String> scenNames = new HashSet<>(inputs.keySet());
        scenNames.addAll(results.keySet());
        for (String scenName : scenNames) {
        	if (scenName != null) {
	        	SimulationStorage.Put put = new SimulationStorage.Put(
	        			inputs.get(scenName), new String[] { scenName, description });
	        	SimulationResults resultData = results.get(scenName); 
	        	put.output = resultData;
	        	if (metricExpressions != null) {
		        	try {
		        		put.metricValues = new MetricValues(resultData, metricExpressions);
		        	} catch (ScriptException e) { /* ignore */ }
	        	}
        		storage.put(put);
        	}
        }
    }

    /**
     * Exports the data of all scenarios in a project, together with
     * the project default external parameter values, and metric values
     * computed using the default external parameter values.
     * @param projectId
     * @param scenarioFile where to save the external parameters, input parameter values,
     *    simulation results, and metric values 
     * @param timeSeriesFile where to save all the time series 
     * @throws ParseException
     * @throws IOException 
     * @throws ScriptException 
     */
    @Transactional(readOnly=true)
    public void exportScenarioData(int projectId, Path scenarioFile, Path timeSeriesFile)
    				throws ParseException, IOException, ScriptException {
        Project project = projectRepository.findOne(projectId);
        Namespace ns = simulationService.makeProjectNamespace(projectId); 
        List<MetricExpression> metrics = simulationService.loadMetricExpressions(project, ns);
        ExportBuilder bld = new ExportBuilder(ns);
        ExtParamValSet set = project.getDefaultextparamvalset();
        String epsName = (set != null) ? set.getName() : "";
        ExternalParameters ext = simulationService.loadExternalParametersFromSet(set, ns);
    	bld.add(ext, epsName);
        for (Scenario scen : project.getScenarios()) {
            SimulationInput in = simulationService.loadSimulationInput(scen, ext);
            bld.add(in, scen.getName());
            SimulationOutput out = simulationService.loadSimulationOutput(scen, in);
            if (out instanceof SimulationResults) {
                bld.add((SimulationResults)out, scen.getName());
	            bld.add(new MetricValues((SimulationResults)out, metrics),
	                    scen.getName(), epsName);
	        }
	    }
        OptimisationProblemIO.writeMulti(bld, scenarioFile);
        OptimisationProblemIO.writeTimeSeries(bld, timeSeriesFile);
    }

    /**
     * Export external parameter sets.
     * All exported sets must be related to the same project.
     */
    @Transactional(readOnly=true)
    public void exportExtParamValSets(
            Path scenarioFile, Path timeSeriesFile, int projectId,
            int... setIds) throws ParseException, IOException {
        Namespace ns = simulationService.makeProjectNamespace(projectId); 
        ExportBuilder bld = new ExportBuilder(ns);
        for (int setId : setIds) {
            ExtParamValSet set = extParamValSetRepository.findOne(setId);
            ExternalParameters ext = simulationService
                    .loadExternalParametersFromSet(set, ns);
            bld.add(ext, set.getName());
        }
        OptimisationProblemIO.writeMulti(bld, scenarioFile);
        OptimisationProblemIO.writeTimeSeries(bld, timeSeriesFile);
    }
    
    /**
     * Export scenario inputs and simulation results.
     * All exported scenarios must be related to the same project.
     * If no results are available for a scenario, only inputs are exported.
     * timeSeriesFile can be left null if no time series are expected. 
     */
    @Transactional(readOnly=true)
    public void exportSimulationResults(
            Path scenarioFile, Path timeSeriesFile, int projectId,
            int... scenIds) throws ParseException, IOException {
        ExternalParameters ext = simulationService.loadExternalParameters(
                projectId, null);
        ExportBuilder bld = new ExportBuilder(ext.getEvaluationSetup());
        for (int scenId : scenIds) {
            Scenario scen = scenarioRepository.findOne(scenId);
            SimulationInput in = simulationService.loadSimulationInput(
                    scen, ext);
            bld.add(in, scen.getName());
            SimulationOutput out = simulationService.loadSimulationOutput(
                    scen, in);
            if (out instanceof SimulationResults) {
                bld.add((SimulationResults)out, scen.getName());
            }
        }
        OptimisationProblemIO.writeMulti(bld, scenarioFile);
        if (timeSeriesFile != null) {
            OptimisationProblemIO.writeTimeSeries(bld, timeSeriesFile);
        }
    }
    
    /**
     * Write metric values.
     * Neither external parameter nor input values are included in the output.
     * Metric values for all available external parameter set and scenario
     * combinations are output.  Absent results are silently omitted.  For
     * now the metric values are computed, not fetched from the database,
     * but that may change at some point.  
     */
    @Transactional(readOnly=true)
    public void exportMetricValues(
            Path scenarioFile, Path timeSeriesFile, int projectId,
            Set<Integer> xpvSetIds, Set<Integer> scenIds)
                    throws ParseException, ScriptException, IOException {
        Project prj = projectRepository.findOne(projectId);
        Namespace ns = simulationService.makeProjectNamespace(prj);
        List<MetricExpression>
                metrics = simulationService.loadMetricExpressions(prj, ns);
        ExportBuilder bld = new ExportBuilder(ns);
        for (Integer setId : xpvSetIds) {
            ExtParamValSet xpvs = extParamValSetRepository.findOne(setId);
            ExternalParameters ext = simulationService
                    .loadExternalParametersFromSet(xpvs, ns);
            for (int scenId : scenIds) {
                Scenario scen = scenarioRepository.findOne(scenId);
                SimulationInput
                        in = simulationService.loadSimulationInput(scen, ext);
                SimulationOutput out = simulationService.loadSimulationOutput(
                        scen, in);
                if (out instanceof SimulationResults) {
                    bld.add(new MetricValues((SimulationResults)out, metrics),
                            scen.getName(), xpvs.getName());
                }
            }
        }
        OptimisationProblemIO.writeMulti(bld, scenarioFile);
        OptimisationProblemIO.writeTimeSeries(bld, timeSeriesFile);
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

        scenarioGenerationService.saveOptimisationProblem(problem, scenarioGenerator, name);
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
        Instant timeOrigin = simulationService.loadTimeOrigin(project.getSimulationmodel());
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
