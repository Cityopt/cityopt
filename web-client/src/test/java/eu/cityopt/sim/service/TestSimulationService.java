package eu.cityopt.sim.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import javax.script.ScriptException;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.util.concurrent.MoreExecutors;

import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationModel;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationModelRepository;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulatorConfigurationException;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
public class TestSimulationService {
    @Autowired
    SimulationService simulationService;

    @Autowired
    SimulationModelRepository simulationModelRepository;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    DataSource dataSource;

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scenario.xml")
    public void testPlumbing() throws Exception {
        loadModel("Plumbing test model", "/testData/plumbing.zip");
        runSimulation();
        dumpTables("plumbing");
    }

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scenario.xml")
    public void testPlumbingAndUpdateMetrics() throws Exception {
        loadModel("Plumbing test model", "/testData/plumbing.zip");
        runSimulation();
        updateMetrics();
        dumpTables("plumbing");
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    public void testModel() throws Exception {
        loadModel("Apros test model", "/testData/testmodel.zip");
        runSimulation();
        //updateMetrics();
        dumpTables("testmodel");
    }

    public void loadModel(String modelName, String modelResource) throws IOException {
        // The simulation test model is not included in the XML test data.
        // Load it from a separate zip file.
        SimulationModel model = 
                simulationModelRepository.findByDescription(modelName).get(0);
        try (InputStream is = this.getClass().getResource(modelResource).openStream()) {
            model.setModelblob(IOUtils.toByteArray(is));
        }
        simulationModelRepository.saveAndFlush(model);
    }

    private void runSimulation() throws ParseException, IOException,
            SimulatorConfigurationException, InterruptedException,
            ExecutionException, ScriptException, Exception {
        Scenario scenario = scenarioRepository.findByName("testscenario").get(0);
        Executor directExecutor = MoreExecutors.directExecutor();
        Future<SimulationOutput> job = simulationService.startSimulation(
                scenario.getScenid(), directExecutor);
        job.get();
    }

    private void updateMetrics() throws ParseException, ScriptException {
        Project project = scenarioRepository.findByName("testscenario").get(0).getProject();
        SimulationService.MetricUpdateStatus status =
                simulationService.updateMetricValues(project.getPrjid(), null);
        System.out.println(status);
    }

    public void dumpTables(String caseName) throws Exception {
        Path outputPath = Paths.get(System.getProperty("java.io.tmpdir"))
                .resolve(caseName + "_result.xml");
        scenarioRepository.flush();
        IDatabaseConnection dbConnection = new DatabaseConnection(
                DataSourceUtils.getConnection(dataSource));
        String[] tableNames = copyIfNotEqual(
                TablesDependencyHelper.getAllDependentTables(dbConnection, "scenario"),
                "simulationmodel");
        IDataSet depDataset = dbConnection.createDataSet(tableNames);
        FlatXmlDataSet.write(depDataset, new FileOutputStream(outputPath.toFile()));
    }

    private String[] copyIfNotEqual(String[] in, String toRemove) {
        List<String> out = new ArrayList<String>();
        for (String s : in) {
            if (!s.equalsIgnoreCase(toRemove)) {
                out.add(s);
            }
        }
        return out.toArray(new String[out.size()]);
    }
}
