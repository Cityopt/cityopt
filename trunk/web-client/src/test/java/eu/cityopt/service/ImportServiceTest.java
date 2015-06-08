package eu.cityopt.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.service.impl.ImportServiceImpl;
import eu.cityopt.sim.service.SimulationTestBase;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })

public class ImportServiceTest extends SimulationTestBase {

	@Autowired
	ImportServiceImpl importService;
	
	@Autowired
	TimeSeriesRepository timeSeriesRepository;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	SimulationResultRepository simulationResultRepository;
	
	@Autowired
	private ScenarioRepository scenarioRepository;
	
	@Autowired
	private MetricValRepository metricValRepository;
	
	@PersistenceContext
	EntityManager em;
	
	@Test
	@Rollback()
	public void testImportTimeSeries() throws EntityNotFoundException, ParseException {
		File timeSeriesInput = new File("./src/test/resources/testData/CSV_testData/timeSeries_aitFormat.csv");
		
		Map<Integer,TimeSeries> tsMap = importService.importTimeSeries(timeSeriesInput);
		for(TimeSeries ts : tsMap.values()){
//			TimeSeries ts = timeSeriesRepository.findOne(id); 
			TimeSeries tsS = timeSeriesRepository.findOne(ts.getTseriesid());
			assertNotNull(tsS);
		}		
	}

	@Test
	@DatabaseSetup({"classpath:/testData/inputParameter_TestData.xml"})
//	@Rollback(false)
	public void testImportExternalParameters() throws EntityNotFoundException, ParseException {
		File epValSetInput = new File("./src/test/resources/testData/CSV_testData/ExtParamSet.csv");
		File timeSeriesInput = new File("./src/test/resources/testData/CSV_testData/timeSeries_aitFormat.csv");
		
		importService.importExtParamValSet(1, epValSetInput, timeSeriesInput);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/inputParameter_TestData.xml"})
//	@Rollback(false)
	public void testImportSimulationResults() throws EntityNotFoundException, ParseException, IOException {
		File simResInput = new File("./src/test/resources/testData/CSV_testData/simulationResults.csv");
		File timeSeriesInput = new File("./src/test/resources/testData/CSV_testData/timeseries.csv");
		
		
//		loadModel("Plumbing test model", "/plumbing.zip");
		
		importService.importSimulationResults(1, simResInput, timeSeriesInput, 1);
		
		List<SimulationResult> simResList = simulationResultRepository.findByScenId(1);
		assertTrue(simResList.size() > 0);
	}

	@Test
	@DatabaseSetup("classpath:/testData/testmodel_scenarios_scenProbCopy.xml")
//	@Rollback(false)
	public void testImportScenarioData() throws Exception {

    	List<File> tseriesfile = new ArrayList<File>();
    	tseriesfile.add(new File("./src/test/resources/testData/CSV_testData/timeseries.csv"));
    	importService.importScenarioData(
    	        1,
    	        Paths.get(getClass().getResource("/test-scenario.csv").toURI()).toFile(),
    	        tseriesfile);
    	em.flush();
    	em.clear();
    	
    	List<MetricVal> mList = metricValRepository.findAll();
    	assertEquals(2,mList.size());
    	assertEquals(1, mList.stream().filter(m -> m.getMetric().getType().getName().equals("Double") ).count());
    	
    	Scenario scen = scenarioRepository.findByNamePrjid("scenario1", 1);
    	Set<InputParamVal> vals = scen.getInputparamvals();
    	for(InputParamVal val : vals)
    		System.out.println(val.getInputparameter().getName());
    	assertEquals(1,scen.getInputparamvals().stream().filter(i -> i.getInputparameter().getName().equals("Burner_efficiency")).count());
    	
    	List<SimulationResult> simResList = simulationResultRepository.findAll();
    	assertEquals(2, simResList.size());
    	for(SimulationResult sr : simResList){
    		assertNotNull(sr.getTimeseries());
    	}
	}
	
}
