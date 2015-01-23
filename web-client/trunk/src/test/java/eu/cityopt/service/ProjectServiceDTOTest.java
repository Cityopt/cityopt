package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
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

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
public class ProjectServiceDTOTest {

	@Autowired
	ProjectService projectService;
	
	@Autowired
	ScenarioService scenarioService;
	
	@PersistenceContext
	EntityManager em;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void findAll() {
		List<ProjectDTO> list = projectService.findAll();
		Assert.notNull(list);
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void findByNameTest() {
		List<ProjectDTO> list = projectService.findByName("project");
		Assert.notNull(list);
		assertTrue(list.size() == 2);
	}
	
	@Test
	public void findByNameTest2() {
		List<ProjectDTO> list = projectService.findByName("notAProjectName");
		Assert.notNull(list);
		assertTrue(list.size() == 0);
	}
	
	@Test
	public void getScenarios() throws EntityNotFoundException {		
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
	public void getComponents() throws EntityNotFoundException {		
		//Scenarios are not loaded with the ProjectDTO object 
		//if needed, load them from the service using the project's ID
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		List<ComponentDTO> components = projectService.getComponents(item.getPrjid());
		Assert.notNull(components);
		ComponentDTO element2 = components.iterator().next();
		assertEquals("testcomponent 1", element2.getName());
	}
	
	@Test
	public void updateProject() throws EntityNotFoundException {		
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		item.setName("new project name");
		item.setDescription("new project description");
		
		projectService.update(item);
		ProjectDTO item2 = projectService.findByID(1);
		assertNotNull(item2);
		assertEquals("new project name", item2.getName());
		assertEquals("new project description", item2.getDescription());
	}
	
	@Test
	public void getMetrics() throws EntityNotFoundException {		
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		Set<MetricDTO> metrics = projectService.getMetrics(item.getPrjid());
		Assert.notNull(metrics);
		MetricDTO met = metrics.iterator().next();
		assertEquals("myMetric", met.getName());
		assertEquals("my expression != bad", met.getExpression()); 
	}
	
	@Test
	public void getExtParams() throws EntityNotFoundException {		
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		Set<ExtParamDTO> metrics = projectService.getExtParams(item.getPrjid());
		Assert.notNull(metrics);
	}
	
	@Test
	public void getSimmulationModel() throws EntityNotFoundException {		
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		Set<ExtParamDTO> metrics = projectService.getExtParams(item.getPrjid());
		Assert.notNull(metrics);
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
