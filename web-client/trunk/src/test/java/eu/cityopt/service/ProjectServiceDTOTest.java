package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import org.springframework.util.Assert;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
public class ProjectServiceDTOTest {

	@Autowired
	ProjectServiceImpl projectService;
	
	@Autowired
	ScenarioServiceImpl scenarioService;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		//ProjectDTO item = projectService.findByID(50);
		//int size = item.getComponents().size();
		List<ProjectDTO> list = projectService.findAll();
		Assert.notNull(list);
	}
	
	@Test
	public void getScenarios() {		
		//Scenarios are not loaded with the ProjectDTO object 
		//if needed, load them from the service using the project's ID
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		Set<ScenarioDTO> scenarios2 = projectService.getScenarios(item.getPrjid());
		Assert.notNull(scenarios2);
		ScenarioDTO element2 = scenarios2.iterator().next();
		assertEquals("this is a test", element2.getDescription());
		assertEquals("test", element2.getName());
	}
	
	@Test
	public void getProjectScenariosDTO() {	
		//Scenarios are directly loaded with the projectScenarioDTO object
		ProjectScenariosDTO item = projectService.findAllWithScenarios().get(0);		
		Assert.notNull(item);
		Set<ScenarioDTO> scenarios = item.getScenarios();
		Assert.notNull(scenarios);
		ScenarioDTO element = scenarios.iterator().next();
		assertEquals("this is a test", element.getDescription());
		assertEquals("test", element.getName());
	}
	
	@Test
	public void setScenariosOnProjectTest() {	
		//Scenarios are directly loaded with the projectScenarioDTO object
		ProjectScenariosDTO item = projectService.findAllWithScenarios().get(0);		
		Assert.notNull(item);
		Set<ScenarioDTO> scenarios = item.getScenarios();
		Assert.notNull(scenarios);
		int sizeBefore = scenarios.size();
		
		ScenarioDTO newScen = new ScenarioDTO();
		newScen.setName("My new Scenario");
		newScen.setDescription("this is my new Scenario");
//		newScen.setPrjid(item.getPrjid());
		scenarios.add(newScen);
		
		projectService.setScenarios(item.getPrjid(), scenarios);
		item = projectService.findAllWithScenarios().get(0);		
		Assert.notNull(item);
		scenarios = item.getScenarios();
		int sizeAfter = scenarios.size();
		
		newScen.setScenid(0);
		assertEquals(sizeBefore +1, sizeAfter);
		//assertTrue(scenarios.contains(newScen));
		
	}
	
	@PersistenceContext
	EntityManager em;
	
	@Test
	public void setScenarioOnScenarioServiceTest() {	
		//Scenarios are directly loaded with the projectScenarioDTO object
		ProjectScenariosDTO item = projectService.findAllWithScenarios().get(0);		
		Assert.notNull(item);
		Set<ScenarioDTO> scenarios = item.getScenarios();
		Assert.notNull(scenarios);
		int sizeBefore = scenarios.size();
		
		ScenarioDTO newScen = new ScenarioDTO();
		newScen.setName("My new Scenario");
		newScen.setDescription("this is my new Scenario");
		
		newScen = scenarioService.save(newScen, item.getPrjid());

		em.flush();
		em.clear();
		ProjectScenariosDTO item2 = projectService.findAllWithScenarios().get(0);		
		Assert.notNull(item2);
		Set<ScenarioDTO> scenarios2 = item2.getScenarios();
		Assert.notNull(scenarios2);
		int sizeAfter = scenarios2.size();
		
		Set<ScenarioDTO> testt = projectService.getScenarios(item.getPrjid());
		sizeAfter = testt.size();
		
		assertEquals(sizeBefore +1, sizeAfter);
		//assertTrue(scenarios.contains(newScen)); //does not work - probably because equals/hashcode are not implemented	
	}
	
}
