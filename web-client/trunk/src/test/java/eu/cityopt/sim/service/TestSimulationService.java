package eu.cityopt.sim.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.After;
import org.junit.Before;
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

import eu.cityopt.model.SimulationModel;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationModelRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.apros.AprosManager;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/scenGen_TestData.xml")
public class TestSimulationService {
    private static final String modelDescription = "plumbing test model";
    private static final String modelPath = "/testData/plumbing.zip";

    @Autowired
    SimulationService simulationService;

    @Autowired
    SimulationModelRepository simulationModelRepository;

    @Autowired
    SimulationResultRepository simulationResultRepository;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    DataSource dataSource;

    @Before
    public void loadModel() throws IOException {
        // The simulation test model is not included in the XML test data.
        // Load it from a separate zip file.
        SimulationModel model = 
                simulationModelRepository.findByDescription(modelDescription).get(0);
        try (InputStream is = this.getClass().getResource(modelPath).openStream()) {
            model.setModelblob(IOUtils.toByteArray(is));
        }
        simulationModelRepository.saveAndFlush(model);
    }

    @Test
    public void testSimulation() throws Exception {
        // TODO: read the path from an appropriate configuration file
        AprosManager.register(Paths.get("C:/users/tteruh/cityopt/git/ss64/profiles"));
        int scenid = scenarioRepository.findByName("testscenario").get(0).getScenid();
        
        Future<SimulationOutput> job = simulationService.startSimulation(scenid);
        job.get();
    }

    @After
    public void dumpTables() throws Exception {
        scenarioRepository.flush();
        IDatabaseConnection dbConnection = new DatabaseConnection(
                DataSourceUtils.getConnection(dataSource));
        String[] depTableNames =
                TablesDependencyHelper.getAllDependentTables(dbConnection, "scenario");
        IDataSet depDataset = dbConnection.createDataSet(depTableNames);
        FlatXmlDataSet.write(depDataset, new FileOutputStream("testSimulation_result.xml"));
    }
}
