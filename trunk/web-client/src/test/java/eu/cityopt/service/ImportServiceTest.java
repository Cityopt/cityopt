package eu.cityopt.service;

import static org.junit.Assert.*;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

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

import eu.cityopt.model.Project;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.TimeSeriesRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })

public class ImportServiceTest {

	@Autowired
	ImportServiceImpl importService;
	
	@Autowired
	TimeSeriesRepository timeSeriesRepository;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Test
	@Rollback()
	public void testImportTimeSeries() throws EntityNotFoundException, ParseException {
		File timeSeriesInput = new File("timeseries.csv");
		
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
		File epValSetInput = new File("ExtParamSet.csv");
		File timeSeriesInput = new File("timeseries.csv");
		
		importService.importExtParamValSet(1, epValSetInput, timeSeriesInput);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/inputParameter_TestData.xml"})
	@Rollback(false)
	public void testImportSimulationResults() throws EntityNotFoundException, ParseException {
		File simResInput = new File("./src/test/resources/testData/CSV_testData/simulationResults.csv");
		File timeSeriesInput = new File("./src/test/resources/testData/CSV_testData/timeseries.csv");
		
		importService.importSimulationResults(1, simResInput, timeSeriesInput, 1);
	}
	
}
