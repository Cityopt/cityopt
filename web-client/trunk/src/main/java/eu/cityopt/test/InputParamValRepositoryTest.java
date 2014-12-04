package eu.cityopt.test;

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
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.Type;
import eu.cityopt.model.Unit;
import eu.cityopt.repository.InputParamValRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
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
	public void FindAll() {
	    List<InputParamVal> result = ipvalRepository.findAll();
	    assertNotEquals(0, result.size());
	}
	
	@Test
	@Rollback(true)
	public void UpdateIPVal() {
	    List<InputParamVal> result = ipvalRepository.findAll();
	    InputParamVal updatedVal = result.get(0);
	    updatedVal.setValue("my new value");
	    Date created = new Date();
	    updatedVal.setCreatedon(created);
	    
	    result = ipvalRepository.findAll();
	    InputParamVal newVal = result.get(0);
	    assertEquals(updatedVal.getValue(), newVal.getValue());
	    assertEquals(updatedVal.getCreatedon(), newVal.getCreatedon());
	}
	
	@Test
	@Rollback(true)
	public void DeleteIPVal() {
	    List<InputParamVal> result = ipvalRepository.findAll();
	    int sizeBefore = result.size();
	    ipvalRepository.delete(result);
	    assertEquals(sizeBefore-1, ipvalRepository.findAll().size());
	}

}
