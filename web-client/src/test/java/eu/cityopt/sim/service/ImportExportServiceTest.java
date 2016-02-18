package eu.cityopt.sim.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.opt.io.JacksonBinder;
import eu.cityopt.opt.io.JacksonBinderScenario;
import eu.cityopt.opt.io.OptimisationProblemIO;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.service.ExtParamService;
import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.sim.eval.util.TempDir;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@DbUnitConfiguration(dataSetLoader=NullReplacementDataSetLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
public class ImportExportServiceTest extends SimulationTestBase {
    @Inject ImportExportService importExportService;
    @Inject ProjectRepository projectRepository;
    @Inject OptimizationSetRepository optimisationSetRepository;
    @Inject ExtParamValSetRepository extParamValSetRepository;
    @Inject ExtParamValSetService extParamValSetService;
    @Inject ExtParamService extParamService;
    @Inject TimeSeriesRepository timeSeriesRepository;

    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    @ExpectedDatabase(value="classpath:/testData/import_model_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testImportModel() throws Exception {
        int projectId = projectRepository.findByNameContainingIgnoreCase("Empty test project").get(0).getPrjid();
        byte[] modelData = getResourceBytes("/testmodel.zip");
        importExportService.importSimulationModel(
                projectId, null, Locale.LanguageRange.parse("en"),
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
        try (InputStream problem = openResource("/test-problem.csv");
             InputStream params = openResource("/ga.properties");
             InputStream ts = openResource("/timeseries.csv")) {
            importExportService.importOptimisationProblem(
                    project.getPrjid(), "testygeneration", problem,
                    null, params, ts);
        }
        dumpTables("import_problem");
    }
    
    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/import_problem_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testExportOptimisationProblem() throws Exception {
        Project project = scenarioRepository.findByNameContaining("testscenario").get(0).getProject();
        try (TempDir tempDir = new TempDir("testimport");
             InputStream problem = openResource("/test-problem.csv");
             InputStream params = openResource("/ga.properties");
             InputStream ts = openResource("/timeseries.csv")) {
            int sgid = importExportService.importOptimisationProblem(
                    project.getPrjid(), "testygeneration", problem, null,
                    params, ts);
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
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    public void testExportExtParamTimeSeries() throws Exception {
        int xpvset = 1;
        ExtParamDTO xp = extParamService.findByName("fuel_cost").get(0);
        //TODO check the result (how?)
        importExportService.exportExtParamTimeSeries(xpvset, System.out, xp);
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    public void testImportExtParamTimeSeries() throws Exception {
        Project project = scenarioRepository.findByNameContaining("testscenario").get(0).getProject();
        int xpvset = 1;
        ExtParamDTO xp = extParamService.findByName("x").get(0);
        Map<String, TimeSeriesDTOX> tsd = null;
        try (InputStream in = getClass().getResource("/timeseries.csv").openStream()) {
            tsd = importExportService.readTimeSeriesCsv(project.getPrjid(), in);
        }
        TimeSeriesDTOX ts = tsd.get("fuel_cost");
        assertNotNull(ts);
        ExtParamValDTO xpv = new ExtParamValDTO();
        xpv.setExtparam(xp);
        extParamValSetService.updateExtParamValInSet(xpvset, xpv, ts);

        ExtParamValSet epvs = extParamValSetRepository.findOne(xpvset);
        assertEquals(3, epvs.getExtparamvalsetcomps().size());
        for (ExtParamValSetComp epvsc : epvs.getExtparamvalsetcomps()) {
            ExtParamVal epv = epvsc.getExtparamval();
            if (epv.getExtparam().getName().equals("x")) {
                assertEquals(3, epv.getTimeseries().getTimeseriesvals().size());
            }
        }
        dumpTables("import_extparamts");
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
        try (TempDir tempDir = new TempDir("testimport");
             InputStream problem = openResource("/test-problem.csv");
             InputStream ts = openResource("/timeseries.csv")) {
            importExportService.importOptimisationSet(
                    project.getPrjid(), null, "testimport", problem, ts);
        }
        dumpTables("import_optset");
    }
    
    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    public void testExportOptimisationSet() throws Exception {
        Project project = scenarioRepository.findByNameContaining(
                "testscenario").get(0).getProject();
        try (TempDir tempDir = new TempDir("testimport");
             InputStream problem = openResource("/test-problem.csv");
             InputStream ts = openResource("/timeseries.csv")) {
            int optSetId = importExportService.importOptimisationSet(
                    project.getPrjid(), null, "testimport", problem, ts);
            Path pout = tempDir.getPath().resolve("problem-out.csv");
            Path tsout = tempDir.getPath().resolve("timeseries-out.csv");
            importExportService.exportOptimisationSet(optSetId, pout, tsout);
            //FIXME Maybe compare the files somehow loosely?
            //Cleaning up for reimport is difficult.
            Files.copy(pout, System.out);
            Files.copy(tsout, System.out);
        }
    }

    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    @ExpectedDatabase(value="classpath:/testData/import_scenarios_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testImportScenarioData() throws Exception {
        Project project = projectRepository.findByNameContainingIgnoreCase("Empty test project").get(0);
        try (InputStream in = openResource("/ost-problem-gs.csv")) {
            importExportService.importSimulationStructure(project.getPrjid(), in);
        }
        try (InputStream scens = openResource(
                "/testData/CSV_testData/gs_scenarios_main.csv");
             InputStream ts = openResource(
                     "/testData/CSV_testData/gs_scenarios_timeseries.csv")) {
            importExportService.importScenarioData(
                    project.getPrjid(), scens,
                    "Imported from gs_scenarios_main.csv", ts);
        }
        importExportService.exportScenarioData(
                project.getPrjid(),
                makeTempPath("imported_scenarios_main.csv"),
                makeTempPath("imported_scenarios_timeseries.csv"));
        dumpTables("import_scenarios");
    }
    
    @Test
    @DatabaseSetup({"classpath:/testData/plumbing_ga_result2.xml"})
    public void testExportMetrics() throws Exception {
        Project prj = projectRepository.findByNameContainingIgnoreCase(
                "Plumbing test").get(0);
        Set<Integer>
            xpvsIds = extParamValSetRepository.findByProject(prj.getPrjid())
                    .stream().map(ExtParamValSet::getExtparamvalsetid)
                    .collect(Collectors.toSet()),
            scenIds = prj.getScenarios().stream().map(Scenario::getScenid)
                    .collect(Collectors.toSet());
        try (TempDir tmp = new TempDir("testExportMetrics")) {
            Path
                scfile = tmp.getPath().resolve("scenarios.csv"),
                tsfile = tmp.getPath().resolve("timeseries.csv");
            importExportService.exportMetricValues(
                    scfile, tsfile, prj.getPrjid(), xpvsIds, scenIds);
            assertFalse("Empty time series file created",
                        Files.exists(tsfile));
            try (InputStream sc = Files.newInputStream(scfile)) {
                JacksonBinderScenario
                    binder = OptimisationProblemIO.readMulti(sc);
                assertEquals("Wrong number of items",
                        4, binder.getItems().size());
                for (JacksonBinderScenario.ScenarioItem
                        it : binder.getItems()) {
                    assertEquals(JacksonBinder.Kind.MET, it.getKind());
                    //TODO Check values or something.
                }
                OptimisationProblemIO.writeMulti(binder, System.out);
            }
        }
    }
}
