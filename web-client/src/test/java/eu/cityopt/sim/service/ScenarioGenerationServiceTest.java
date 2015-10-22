package eu.cityopt.sim.service;


import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.script.ScriptException;

import org.hamcrest.core.StringContains;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import eu.cityopt.model.Algorithm;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.AlgorithmRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.opt.OptimisationResults;
import eu.cityopt.sim.opt.Solution;

/**
 * Basic testing for scenario generation.
 *
 * After every test, the database content is dumped to the system %TEMP%
 * folder: see files *_result.xml.  The expected results files are produced
 * from these with the XSL script src/test/resources/xslt/drop-ids.xsl. 
 *
 * @author Hannu Rummukainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@DatabaseTearDown(value="classpath:/testData/cleantables.xml",
        type=DatabaseOperation.DELETE_ALL)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
public class ScenarioGenerationServiceTest extends SimulationTestBase {
    @Inject ScenarioGenerationService scenarioGenerationService;
    @Inject ScenarioGeneratorRepository scenarioGeneratorRepository;
    @Inject AlgorithmRepository algorithmRepository;
    @Inject ImportExportService importExportService;
    @Inject ProjectRepository projectRepository;

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scengen.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_gridsearch_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbingGridSearch() throws Exception {
        loadModel("Plumbing test model", "/plumbing.zip");
        runScenarioGeneration("grid search");
        dumpTables("plumbing_gridsearch");
    }

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scengen.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_ga_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbingGA() throws Exception {
        loadModel("Plumbing test model", "/plumbing.zip");
        runScenarioGeneration("genetic algorithm");
        dumpTables("plumbing_ga");
    }

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scengen_noext.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_ga_noext_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbingGA_noExt() throws Exception {
        loadModel("Plumbing test model", "/plumbing.zip");
        runScenarioGeneration("genetic algorithm");
        dumpTables("plumbing_ga_noext");
    }

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scengen_error.xml")
    public void testPumbingGA_SyntaxError() throws Exception {
        loadModel("Plumbing test model", "/plumbing.zip");
        try {
            runScenarioGeneration("genetic algorithm");
            fail();
        } catch (ScriptException e) {
            assertThat(e.getMessage(), StringContains.containsString(
                    "In input C01.typ: name 'symmetric' is not defined"));
            assertThat(e.getMessage(), StringContains.containsString(
                    "In constraint con1: name 'meanie' is not defined"));
            assertThat(e.getMessage(), StringContains.containsString(
                    "In objective obj1: name 'Infinyty' is not defined"));
        }
        dumpTables("plumbing_scengen_error");
    }

    @Test
    @Ignore("This can take a long time")
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    public void testImportedProblemGA() throws Exception {
        Project project = projectRepository.findByNameContainingIgnoreCase("Empty test project").get(0);
        byte[] modelData = getResourceBytes("/ost.zip");
        importExportService.importSimulationModel(
                project.getPrjid(), null, "test project",
                modelData, "Apros-Combustion-5.13.06-64bit",
                Instant.parse("2015-01-01T00:00:00Z"));
        Integer scId = null;
        String problemRes = "/ost-problem.csv";
        try (InputStream in = openResource(problemRes)) {
            importExportService.importSimulationStructure(project.getPrjid(),
                                                          in);
        }
        try (InputStream problem = openResource(problemRes);
             InputStream params = openResource("/ga.properties");
             InputStream ts = openResource("/timeseries.csv")) {
            scId = importExportService.importOptimisationProblem(
                    project.getPrjid(), "import optimisation test",
                    problem, null, params, ts);
        }
        runScenarioGeneration(scId, "imported");
        dumpTables("imported_ga");
    }

    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    public void testImportedProblemGridSearch() throws Exception {
        Project project = projectRepository.findByNameContainingIgnoreCase("Empty test project").get(0);
        byte[] modelData = getResourceBytes("/ost.zip");
        importExportService.importSimulationModel(
                project.getPrjid(), null, "test project",
                modelData, "Apros-Combustion-5.13.06-64bit",
                Instant.parse("2015-01-01T00:00:00Z"));
        Integer scId = null;
        String problemRes = "/ost-problem-gs.csv";
        try (InputStream in = openResource(problemRes)) {
            importExportService.importSimulationStructure(project.getPrjid(),
                                                          in);
        }
        try (InputStream problem = openResource(problemRes);
             InputStream params = openResource("/gs.properties");
             InputStream ts = openResource("/timeseries.csv")) {
            scId = importExportService.importOptimisationProblem(
                    project.getPrjid(), "import optimisation test",
                    problem, null, params, ts);
        }
        runScenarioGeneration(scId, "imported");
        dumpTables("imported_gs");
        importExportService.exportScenarioData(
        		project.getPrjid(), makeTempPath("gs_scenarios_main.csv"),
        		makeTempPath("gs_scenarios_timeseries.csv"));
    }

    private void runScenarioGeneration(String algorithmName) throws Exception {
        ScenarioGenerator scenarioGenerator =
                scenarioGeneratorRepository.findAll().iterator().next();
        scenarioGenerator.setAlgorithm(
                algorithmRepository.save(findAlgorithm(algorithmName)));
        scenarioGeneratorRepository.save(scenarioGenerator);
        runScenarioGeneration(scenarioGenerator.getScengenid(), algorithmName);
    }

    private void runScenarioGeneration(int scenGenId, String algorithmName) throws Exception {
        Future<OptimisationResults> job =
                scenarioGenerationService.startOptimisation(scenGenId, null);
        OptimisationResults results = job.get();
        System.out.println("Results of " + algorithmName + ":");
        printResults(results);
    }

    private void printResults(OptimisationResults results) {
        System.out.println("status = " + results.status);
        System.out.println("paretoFront = {");
        for (Solution solution : results.paretoFront) {
            System.out.println("  {");
            System.out.println("    input = " + solution.input);
            System.out.println("    infeasibilities = " + Arrays.toString(solution.constraintStatus.infeasibilities));
            System.out.println("    objectiveValues = " + Arrays.toString(solution.objectiveStatus.objectiveValues));
            System.out.println("  }");
        }
        System.out.println("}");
    }

    private Algorithm findAlgorithm(String name) throws EntityNotFoundException {
        for (Algorithm a : algorithmRepository.findAll()) {
            if (a.getDescription().equalsIgnoreCase(name)) {
                return a;
            }
        }
        throw new EntityNotFoundException("Algorithm " + name);
    }
}
