package eu.cityopt.sim.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.script.ScriptException;

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
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulationOutput;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
public class TestSimulationService extends SimulationTestBase {
    @Autowired
    SimulationService simulationService;

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbing() throws Exception {
        loadModel("Plumbing test model", "/testData/plumbing.zip");
        runSimulation();
        dumpTables("plumbing");
    }

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_metrics_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbingAndUpdateMetrics() throws Exception {
        loadModel("Plumbing test model", "/testData/plumbing.zip");
        runSimulation();
        updateMetrics();
        dumpTables("plumbing_metrics");
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    @ExpectedDatabase(value="classpath:/testData/testmodel_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testModel() throws Exception {
        loadModel("Apros test model", "/testData/testmodel.zip");
        runSimulation();
        //updateMetrics();
        dumpTables("testmodel");
    }

    private void runSimulation() throws ParseException, IOException,
            ConfigurationException, InterruptedException,
            ExecutionException, ScriptException, Exception {
        Scenario scenario = scenarioRepository.findByName("testscenario").get(0);
        Queue<Runnable> tasks = new ArrayDeque<>();
        Future<SimulationOutput> job = simulationService.startSimulation(
                scenario.getScenid(), tasks::add);
        job.get();
        while (!tasks.isEmpty()) {
            tasks.poll().run();
        }
    }

    private void updateMetrics() throws ParseException, ScriptException {
        Project project = scenarioRepository.findByName("testscenario").get(0).getProject();
        SimulationService.MetricUpdateStatus status =
                simulationService.updateMetricValues(project.getPrjid(), null);
        System.out.println(status);
    }
}
