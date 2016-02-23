package eu.cityopt.sim.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptException;

import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulationOutput;

/**
 * Test simulation runs via the SimulationService class.
 *
 * We use the ExpectedDatabase annotation to check the test results.
 * The expected results are manually reviewed results from earlier
 * JUnit test runs.
 * 
 * After every test, the database content is dumped to the system %TEMP%
 * folder: see files *_result.xml.  The expected results files are produced
 * from these with the XSL script src/test/resources/xslt/drop-ids.xsl. 
 *
 * @author Hannu Rummukainen
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@DbUnitConfiguration(dataSetLoader=NullReplacementDataSetLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
public class SimulationServiceTest extends SimulationTestBase {
    @Autowired
    SimulationService simulationService;

    @Autowired
    ImportExportService importExportService;

    @Autowired
    ProjectRepository projectRepository;

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbing() throws Exception {
        loadModel("Plumbing test model", "/plumbing.zip");
        runSimulation();
        dumpTables("plumbing");
    }

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scenario_noext.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_noext_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbing_noExt() throws Exception {
        loadModel("Plumbing test model", "/plumbing.zip");
        runSimulation();
        dumpTables("plumbing_noext");
    }

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_metrics_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbingAndUpdateMetrics() throws Exception {
        loadModel("Plumbing test model", "/plumbing.zip");
        runSimulation();
        updateMetrics();
        dumpTables("plumbing_metrics");
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/testmodel_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testModel() throws Exception {
        loadModel("Apros test model", "/testmodel.zip");
        runSimulation();
        //updateMetrics();
        dumpTables("testmodel");
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    public void testModel_cancel() throws Exception {
        loadModel("Apros test model", "/testmodel.zip");
        Scenario scenario = scenarioRepository.findByNameContaining("testscenario").get(0);
        BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
        Future<SimulationOutput> job = simulationService.startSimulation(
                scenario.getScenid(), tasks::add);
        // The cancellation runs the clean-up task synchronously.
        job.cancel(true);
        assertTrue(job.isCancelled());
        assertTrue(tasks.isEmpty());
        // Just in case some task got run in another thread...
        Thread.sleep(TimeUnit.SECONDS.toMillis(3));
        assertTrue(tasks.isEmpty());
        // There should be no updates to the database.
        scenario = scenarioRepository.findByNameContaining("testscenario").get(0);
        assertNull(scenario.getStatus());
        dumpTables("cancel");
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario_error.xml")
    public void testModel_SyntaxError() throws Exception {
        loadModel("Apros test model", "/testmodel.zip");
        try {
            runSimulation();
            fail();
        } catch (ScriptException e) {
            assertThat(e.getMessage(), StringContains.containsString(
                    "In metric fuelconsumption: name 'integrat' is not defined"));
            assertThat(e.getMessage(), StringContains.containsString(
                    "In metric fuelcost: name 'fue_cost' is not defined"));
        }
        dumpTables("testmodel_error");
    }

    private void runSimulation() throws ParseException, IOException,
            ConfigurationException, InterruptedException,
            ExecutionException, ScriptException, Exception {
        Scenario scenario = scenarioRepository.findByNameContaining("testscenario").get(0);
    	runSimulation(scenario.getScenid());
    }

	private void runSimulation(int scenId) throws ParseException, IOException,
			ConfigurationException, InterruptedException, ExecutionException,
			ScriptException, Exception {
		BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
		Future<SimulationOutput> job =
				simulationService.startSimulation(scenId, tasks::add);
		do {
			tasks.take().run();
		} while (!tasks.isEmpty());
		assertTrue(job.isDone());
	}

    private void updateMetrics() throws ParseException, ScriptException {
        Project project = scenarioRepository.findByNameContaining("testscenario").get(0).getProject();
        SimulationService.MetricUpdateStatus status =
                simulationService.updateMetricValues(project.getPrjid(), null);
        System.out.println(status);
    }

    @Test
    @DatabaseSetup("classpath:/testData/empty_project.xml")
    @ExpectedDatabase(value="classpath:/testData/imported_sim_result.xml",
            assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testSimulateImportedScenario() throws Exception {
        Project project = projectRepository.findByNameContainingIgnoreCase(
                "Empty test project").get(0);
        byte[] modelData = getResourceBytes("/ost.zip");
        importExportService.importSimulationModel(
                project.getPrjid(), null, Locale.LanguageRange.parse("en"),
                modelData, "Apros-Combustion-5.13.06-64bit",
                Instant.parse("2015-01-01T00:00:00Z"));
        String scenarioRes = "/testData/import_sim_scenarios.csv"; 
        try (InputStream in = openResource(scenarioRes)) {
            importExportService.importSimulationStructure(
                    project.getPrjid(), in);
        }
        try (InputStream scenarios = openResource(scenarioRes);
             InputStream ts = openResource(
                     "/testData/import_sim_timeseries.csv")){
            importExportService.importScenarioData(
                    project.getPrjid(), scenarios,
                    "Imported from " + scenarioRes, ts);
        }
        Scenario scenario = scenarioRepository.findByNameContaining(
                "new scen 789").get(0);
        runSimulation(scenario.getScenid());
        dumpTables("imported_sim");
    }

}
