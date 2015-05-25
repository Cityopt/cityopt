package eu.cityopt.sim.service;

import java.nio.file.Path;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import eu.cityopt.model.Project;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.sim.eval.util.TempDir;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@DbUnitConfiguration(dataSetLoader=NullReplacementDataSetLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
public class TestImportExportService extends SimulationTestBase {
    @Inject ImportExportService importExportService;
    @Inject ProjectRepository projectRepository;

    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    @ExpectedDatabase(value="classpath:/testData/import_model_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testImportModel() throws Exception {
        int projectId = projectRepository.findByName("Empty test project").get(0).getPrjid();
        byte[] modelData = getResourceBytes("/testmodel.zip");
        importExportService.importSimulationModel(
                projectId, null, "test project",
                modelData, null, null);
        String warnings = importExportService.importModelInputsAndOutputs(projectId, 0);
        if (warnings != null) {
            System.err.println("Warnings from import:");
            System.err.println(warnings);
        }
        dumpTables("import_model", true);
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/import_problem_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testImportOptimisationProblem() throws Exception {
        Project project = scenarioRepository.findByNameContaining("testscenario").get(0).getProject();
        try (TempDir tempDir = new TempDir("testimport")) {
            Path problemPath = copyResource("/test-problem.csv", tempDir);
            Path paramPath = copyResource("/ga.properties", tempDir);
            Path tsPath = copyResource("/timeseries.csv", tempDir);
            importExportService.importOptimisationProblem(
                    project.getPrjid(), "testygeneration", problemPath, null, paramPath, tsPath);
        }
        dumpTables("import_problem");
    }

    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    @ExpectedDatabase(value="classpath:/testData/import_structure_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testImportProjectStructure() throws Exception {
        Project project = projectRepository.findByName("Empty test project").get(0);
        try (TempDir tempDir = new TempDir("testimport")) {
            Path problemPath = copyResource("/test-problem.csv", tempDir);
            importExportService.importSimulationStructure(project.getPrjid(), problemPath);
        }
        dumpTables("import_structure");
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/import_optset_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testImportOptimisationSet() throws Exception {
        Project project = scenarioRepository.findByNameContaining("testscenario").get(0).getProject();
        try (TempDir tempDir = new TempDir("testimport")) {
            Path problemPath = copyResource("/test-problem.csv", tempDir);
            Path tsPath = copyResource("/timeseries.csv", tempDir);
            importExportService.importOptimisationSet(
                    project.getPrjid(), null, "testimport", problemPath, tsPath);
        }
        dumpTables("import_optset");
    }
}
