package eu.cityopt.repository;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
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

import eu.cityopt.DTO.ComponentInputParamDTO;
import eu.cityopt.model.Component;
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
	private ComponentRepository crepo;
	
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
	 		System.out.print(item.getInputparamvalid() + " | ");
	 		System.out.print(item.getValue() + " | ");
	 		System.out.print(item.getScenarioid() + " | ");
	 		System.out.print(item.getPrjid() + " | ");
	 		System.out.println();
	 	}
	 	assertNotNull(list);
	 	assertEquals(4, list.stream().filter(i -> i.getComponentname().equals("Solar_thermal_panels")).count());
	 	assertEquals(4, list.stream().filter(i -> i.getComponentname().equals("Storage_Vertical_tank_with_heat_structure")).count());
	 	assertEquals(5, list.stream().filter(i -> i.getComponentname().equals("Gas_boiler")).count());
	}
	
	@Test
	public void testfindComponentsWithInputParams2() throws SQLException {
		
		List<ComponentInputParamDTO> component_1 = cqrepo.findComponentsWithInputParamsByCompId(1);
		assertEquals(4,component_1.size());
		
		List<ComponentInputParamDTO> component_2 = cqrepo.findComponentsWithInputParamsByCompId(2);
		assertEquals(4,component_2.size());
		
		List<ComponentInputParamDTO> component_3 = cqrepo.findComponentsWithInputParamsByCompId(3);
		assertEquals(5,component_3.size());
	 	
	}
	
	
	
	@Test
	public void testTSBatchInsert() {
	 	List<TimeSeriesVal> list = new ArrayList<TimeSeriesVal>();
	 	
	 	Calendar cal = Calendar.getInstance();

	 	for(int i =0; i < 8000; i++) {
	 		TimeSeriesVal tsV = new TimeSeriesVal();
	 		tsV.setTime(cal.getTime());
	 		tsV.setValue(i);
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
	 		tsV.setValue(i);
	 		list.add(tsV);
	 	}
	 	
	 	timeSeriesValRepository.save(list);
	}
}
