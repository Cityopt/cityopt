package eu.cityopt.repository;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.ComponentInputParamDTO;
import eu.cityopt.model.TimeSeriesVal;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml"})
public class CustomQueryRepositoryTest {

	@Autowired
	private CustomQueryRepository cqrepo; 
	
	@Autowired
	private TimeSeriesValRepository timeSeriesValRepository; 
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testfindComponentsWithInputParams() throws SQLException {
	 	List<ComponentInputParamDTO> list = cqrepo.findComponentsWithInputParams(1, 1);
	 	for(ComponentInputParamDTO item : list) {
	 		System.out.print(item.getComponentid() + " | ");
	 		System.out.print(item.getComponentname() + " | ");
	 		System.out.print(item.getInputid() + " | ");
	 		System.out.print(item.getInputparametername() + " | ");
	 		System.out.print(item.getScendefinitionid() + " | ");
	 		System.out.print(item.getValue() + " | ");
	 		System.out.print(item.getScenarioid() + " | ");
	 		System.out.print(item.getPrjid() + " | ");
	 		System.out.println();
	 	}
	 	assertNotNull(list);
	}
	
	@Test
	public void testTSBatchInsert() {
	 	List<TimeSeriesVal> list = new ArrayList<TimeSeriesVal>();
	 	
	 	Calendar cal = Calendar.getInstance();

	 	for(int i =0; i < 8000; i++) {
	 		TimeSeriesVal tsV = new TimeSeriesVal();
	 		tsV.setTime(cal.getTime());
	 		tsV.setValue(Integer.toString(i));
	 		list.add(tsV);
	 	}
	 	
	 	cqrepo.insertTimeSeriesBatch(list);
	}

	@Test
	public void testTSBatchRepositoryInsert() {
	 	List<TimeSeriesVal> list = new ArrayList<TimeSeriesVal>();
	 	
	 	Calendar cal = Calendar.getInstance();

	 	for(int i =0; i < 8000; i++) {
	 		TimeSeriesVal tsV = new TimeSeriesVal();
	 		tsV.setTime(cal.getTime());
	 		tsV.setValue(Integer.toString(i));
	 		list.add(tsV);
	 	}
	 	
	 	timeSeriesValRepository.save(list);
	}
}
