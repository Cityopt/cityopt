package eu.cityopt.repository;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.After;
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

import eu.cityopt.model.InputParamVal;
import eu.cityopt.repository.InputParamValRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })

public class InputParamValRepositoryTest {
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	ScenarioRepository scenarioRepository;
	
	@Autowired
	InputParamValRepository ipvalRepository;
	
	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
	public void FindAll() {
	    List<InputParamVal> result = ipvalRepository.findAll();
	    assertNotEquals(0, result.size());
	}
	
	@Test
	@Rollback(true)
	@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
	public void UpdateIPVal() {
	    List<InputParamVal> result = ipvalRepository.findAll();
	    InputParamVal value = result.get(0);
	    value.setValue("my new value");
	    Date created = new Date();
	    value.setCreatedon(created);
	    
	    InputParamVal newVal = ipvalRepository.findOne(value.getInputparamvalid());
	    assertEquals(value.getValue(), newVal.getValue());
	    assertEquals(value.getCreatedon(), newVal.getCreatedon());
	}
	
	@Test
	@Rollback(true)
	@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
	public void DeleteIPVal() {
	    List<InputParamVal> result = ipvalRepository.findAll();
//	    int sizeBefore = result.size();
	    ipvalRepository.delete(result);
	    assertEquals(0, ipvalRepository.findAll().size());
	}
	
	@Test
	@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
	public void testfindByInputIdAndScenId() {
	
	InputParamVal result = ipvalRepository.findByInputIdAndScenId(2, 1);
	
	assertNotNull(result);
	assertEquals("10",result.getValue());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml"})
	public void findByComponentAndScenarioTest(){
		
		List<InputParamVal> result = ipvalRepository.findByComponentAndScenario(2, 1);
		
		assertNotNull(result);
		assertEquals(4,result.size());
		assertEquals(4,result.stream().filter(r -> r.getInputparameter()
				.getComponent().getName().equals("Storage_Vertical_tank_with_heat_structure")).count());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC2.xml"})
	public void findByNameAndScenarioTest(){
		
		InputParamVal result = ipvalRepository.findByNameAndScenario("collector_area", 1);
		
		assertNotNull(result);
		assertEquals("100", result.getValue());
		
		result = ipvalRepository.findByNameAndScenario("Lower_heating_value", 2);
		assertNotNull(result);
		assertEquals("47.14", result.getValue());
	}

}
