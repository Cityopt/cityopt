package eu.cityopt.sim.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.script.ScriptException;

import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Algorithm;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.opt.io.CsvTimeSeriesData;
import eu.cityopt.opt.io.OptimisationProblemIO;
import eu.cityopt.opt.io.TimeSeriesData;
import eu.cityopt.repository.AlgorithmRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.opt.AlgorithmParameters;
import eu.cityopt.sim.opt.OptimisationProblem;

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

    @Inject ScenarioGenerationService scenarioGenerationService;

    @Inject private ProjectRepository projectRepository;
    @Inject private AlgorithmRepository algorithmRepository;
    @Inject private ScenarioGeneratorRepository scenarioGeneratorRepository;
    @Inject private SimulationService simulationService;

    /** 
     * Creates a new ScenarioGenerator row from text files.
     * @param projectId the associated project, which must have exactly the
     *   same external parameters, input parameters, output variables and
     *   metrics as the optimisation problem.
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
                OptimisationProblemIO.readCsv(problemFile, timeSeriesData);

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
        EvaluationSetup setup = new EvaluationSetup(
                simulationService.getEvaluator(), timeOrigin);
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
