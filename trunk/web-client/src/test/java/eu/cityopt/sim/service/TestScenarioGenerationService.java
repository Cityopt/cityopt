package eu.cityopt.sim.service;

import java.io.IOException;
import java.text.ParseException;
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

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.opt.OptimisationResults;

/**
 * Basic testing for scenario generation.
 *
 * The expected results file plumbing_scengen_result.xml is produced by
 * the drop-ids.xsl script: see TestSimulationService documentation. 
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
public class TestScenarioGenerationService extends SimulationTestBase {
    @Autowired ScenarioGenerationService scenarioGenerationService;
    @Autowired ScenarioGeneratorRepository scenarioGeneratorRepository;

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scengen.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_scengen_result.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testPlumbing() throws Exception {
        loadModel("Plumbing test model", "/testData/plumbing.zip");
        runScenarioGeneration();
        dumpTables("plumbing_scengen");
    }

    private void runScenarioGeneration() throws ParseException, IOException,
            ConfigurationException, InterruptedException,
            ExecutionException, ScriptException, Exception {
        ScenarioGenerator scenarioGenerator =
                scenarioGeneratorRepository.findAll().iterator().next();
        Future<OptimisationResults> job = scenarioGenerationService.startOptimisation(
                scenarioGenerator.getScengenid());
        job.get();
    }
}
