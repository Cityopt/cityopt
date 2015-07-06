package eu.cityopt.sim.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
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
import eu.cityopt.repository.OptimizationSetRepository;
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
    @Inject OptimizationSetRepository optimisationSetRepository;

    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    @ExpectedDatabase(value="classpath:/testData/import_model_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testImportModel() throws Exception {
        int projectId = projectRepository.findByNameContainingIgnoreCase("Empty test project").get(0).getPrjid();
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
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/import_problem_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testExportOptimisationProblem() throws Exception {
        Project project = scenarioRepository.findByNameContaining("testscenario").get(0).getProject();
        try (TempDir tempDir = new TempDir("testimport")) {
            Path problemPath = copyResource("/test-problem.csv", tempDir);
            Path paramPath = copyResource("/ga.properties", tempDir);
            Path tsPath = copyResource("/timeseries.csv", tempDir);
            int sgid = importExportService.importOptimisationProblem(
                    project.getPrjid(), "testygeneration", problemPath, null,
                    paramPath, tsPath);
            Path pout = tempDir.getPath().resolve("problem-out.csv");
            Path tsout = tempDir.getPath().resolve("timeseries-out.csv");
            importExportService.exportOptimisationProblem(sgid, pout, tsout);
            //FIXME Maybe compare the files somehow loosely?
            //Cleaning up for reimport is difficult.
            Files.copy(pout, System.out);
            Files.copy(tsout, System.out);
        }
    }

    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    @ExpectedDatabase(value="classpath:/testData/import_structure_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testImportProjectStructure() throws Exception {
        Project project = projectRepository.findByNameContainingIgnoreCase("Empty test project").get(0);
        try (InputStream in = getClass().getResource("/test-problem.csv").openStream()) {
            importExportService.importSimulationStructure(project.getPrjid(), in);
        }
        dumpTables("import_structure");
    }
    
    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    @ExpectedDatabase(
            value="classpath:/testData/import_structure_result.xml",
            assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testReimportProjectStructure() throws Exception {
        Project project = projectRepository
                .findByNameContainingIgnoreCase("Empty test project").get(0);
        int pid = project.getPrjid();
        try (InputStream
                in = getClass().getResourceAsStream("/test-problem.csv")) {
            importExportService.importSimulationStructure(pid, in);
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        importExportService.exportSimulationStructure(pid, System.out);
        importExportService.exportSimulationStructure(pid, bout);
        String name = project.getName(), loc = project.getLocation();
        projectRepository.delete(pid);
        //JPA workaround
        projectRepository.flush();
        Project pnew = new Project();
        pnew.setName(name);
        pnew.setLocation(loc);
        pnew = projectRepository.save(pnew);
        importExportService.importSimulationStructure(
                pnew.getPrjid(), new ByteArrayInputStream(bout.toByteArray()));
        //dumpTables workaround
        projectRepository.flush();
        dumpTables("reimport_structure");
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
    
    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    public void testExportOptimisationSet() throws Exception {
        Project project = scenarioRepository.findByNameContaining(
                "testscenario").get(0).getProject();
        try (TempDir tempDir = new TempDir("testimport")) {
            Path problemPath = copyResource("/test-problem.csv", tempDir);
            Path tsPath = copyResource("/timeseries.csv", tempDir);
            int optSetId = importExportService.importOptimisationSet(
                    project.getPrjid(), null, "testimport",
                    problemPath, tsPath);
            Path pout = tempDir.getPath().resolve("problem-out.csv");
            Path tsout = tempDir.getPath().resolve("timeseries-out.csv");
            importExportService.exportOptimisationSet(optSetId, pout, tsout);
            //FIXME Maybe compare the files somehow loosely?
            //Cleaning up for reimport is difficult.
            Files.copy(pout, System.out);
            Files.copy(tsout, System.out);
        }
    }
}
