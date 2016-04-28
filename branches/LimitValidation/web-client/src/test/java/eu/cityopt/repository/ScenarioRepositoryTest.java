/**
 * 
 */
package eu.cityopt.repository;

import static org.junit.Assert.*;

import java.util.Calendar;
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

import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;

/**
 * @author MayerhoferM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
public class ScenarioRepositoryTest {
	
	@Autowired
	ScenarioRepository scenarioRepository;

	@Autowired
	ProjectRepository projectRepository;
	
	String testScenName = "test";
	String testScenDescription = "this is a test";
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	public void findScenarioByName() {
		List<Scenario> resList = scenarioRepository.findByNameContaining(testScenName);
		Scenario result = resList.get(0);
		assertNotNull(result);
		assertEquals(testScenName, result.getName());
		assertEquals(testScenDescription, result.getDescription());
	}
	
	@Test
	@Rollback(true)
	public void updateScenario() {
		List<Scenario> resList = scenarioRepository.findByNameContaining(testScenName);
		Scenario result = resList.get(0);
		
		result.setDescription("this description is much better");
		result.setUpdatedon(new Date());
		scenarioRepository.saveAndFlush(result);
		
		resList = scenarioRepository.findByNameContaining(testScenName);
		Scenario result2 = resList.get(0);
		assertEquals(result, result2);
	}

	@Test
	@Rollback(true)
	public void deleteScenario() {
		List<Scenario> resList = scenarioRepository.findByNameContaining(testScenName);
		Scenario result = resList.get(0);
		
		scenarioRepository.delete(result);
		
		resList = scenarioRepository.findByNameContaining(testScenName);
		assertTrue(resList.isEmpty());
	}
	
	@Test
	@Rollback(true)
	public void findAllScenario() {
		List<Scenario> resList = scenarioRepository.findAll();
		
		assertFalse(resList.isEmpty());
	}
	
	@Test
	@Rollback(true)
	public void findByDate() {
		
		Project project = new Project();
		project.setName("Project 2");
		project.setLocation("Helsinki");
		project = projectRepository.save(project);
		
		//generate scenario from year 2013
		Scenario testScenario = new Scenario(); 
		testScenario.setName(testScenName);
		testScenario.setDescription(testScenDescription);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2013);
		testScenario.setCreatedon(cal.getTime());
		testScenario.setUpdatedon(new Date());
		testScenario.setProject(project);
		
		scenarioRepository.saveAndFlush(testScenario);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.set(2014, Calendar.NOVEMBER, 1);
		Date testScenLower = cal2.getTime();
		
		//scenario from 2013 should not be found
		List<Scenario> resList = scenarioRepository.findByCreationDate(testScenLower, new Date());
		assertEquals(1, resList.size());
	}
}
