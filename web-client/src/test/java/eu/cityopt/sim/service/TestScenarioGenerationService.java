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

import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.opt.OptimisationResults;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
public class TestScenarioGenerationService extends SimulationTestBase {
    @Autowired ScenarioGenerationService scenarioGenerationService;
    @Autowired ScenarioGeneratorRepository scenarioGeneratorRepository;

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scengen.xml")
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
        Queue<Runnable> tasks = new ArrayDeque<>();
        Future<OptimisationResults> job = scenarioGenerationService.startOptimisation(
                scenarioGenerator.getScengenid(), tasks::add);
        while (!tasks.isEmpty()) {
            tasks.poll().run();
        }
        job.get();
        while (!tasks.isEmpty()) {
            tasks.poll().run();
        }
    }
}
