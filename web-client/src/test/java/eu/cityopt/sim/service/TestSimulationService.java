package eu.cityopt.sim.service;

import static org.junit.Assert.assertEquals;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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
    public void testTimeConversion() {
        Instant timeOrigin = Instant.ofEpochMilli(123456);
        SimulationService s = simulationService;
        assertEquals(new Date(124456), s.toDate(1, timeOrigin));
        assertEquals(2.0, s.toSimTime(new Date(125456), timeOrigin), 0.0);
        assertEquals(999, s.toSimTime(s.toDate(999, timeOrigin), timeOrigin), 0.0);
    }

    @Test
    @DatabaseSetup("classpath:/testData/plumbing_scenario.xml")
    public void testPlumbing() throws Exception {
        loadModel("Plumbing test model", "/testData/plumbing.zip");
        runSimulation();
        dumpTables("plumbing");
    }

    @Test
    @DatabaseSetup("classpath:/testData/testmodel_scenario.xml")
    public void testModel() throws Exception {
        loadModel("Apros test model", "/testData/testmodel.zip");
        runSimulation();
        dumpTables("testmodel");
    }

    public void testParallelRuns() throws Exception {
        loadModel("Plumbing test model", "/testData/plumbing.zip");
        runSimulation();
        dumpTables("plumbing");
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
        Callable<SimulationOutput> job = simulationService.makeSimulationJob(scenario);
        job.call();
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
