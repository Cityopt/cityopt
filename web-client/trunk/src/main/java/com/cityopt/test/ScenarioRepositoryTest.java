/**
 * 
 */
package com.cityopt.test;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.Project;
import com.cityopt.model.Scenario;
import com.cityopt.repository.ProjectRepository;
import com.cityopt.repository.ScenarioRepository;

/**
 * @author MayerhoferM
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-context.xml" })
@Transactional
public class ScenarioRepositoryTest {
	
	@Autowired
	ScenarioRepository scenarioRepository;

	@Autowired
	ProjectRepository projectRepository;
	
	String testScenName = "test";
	String testScenDescription = "this is a test";
	Date testScenCreatedon = new Date();
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		scenarioRepository.deleteAll();
		projectRepository.deleteAll();
		
		Project project = new Project();
		project.setName("Project 1");
		project.setLocation("Vienna");
		
		Scenario testScenario = new Scenario(); 
		testScenario.setName(testScenName);
		testScenario.setDescription(testScenDescription);
		testScenario.setCreatedon(testScenCreatedon);
		testScenario.setUpdatedon(new Date());
		testScenario.setProject(project);
		
		scenarioRepository.saveAndFlush(testScenario);
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
		List<Scenario> resList = scenarioRepository.findByName(testScenName);
		Scenario result = resList.get(0);
		assertNotNull(result);
		assertEquals(result.getName(), testScenName);
		assertEquals(result.getDescription(), testScenDescription);
		assertEquals(result.getCreatedon(), testScenCreatedon);	
	}
	
	@Test
	@Rollback(true)
	public void updateScenario() {
		List<Scenario> resList = scenarioRepository.findByName(testScenName);
		Scenario result = resList.get(0);
		
		result.setDescription("this description is much better");
		result.setUpdatedon(new Date());
		scenarioRepository.saveAndFlush(result);
		
		resList = scenarioRepository.findByName(testScenName);
		Scenario result2 = resList.get(0);
		assertEquals(result, result2);
	}

	@Test
	@Rollback(true)
	public void deleteScenario() {
		List<Scenario> resList = scenarioRepository.findByName(testScenName);
		Scenario result = resList.get(0);
		
		scenarioRepository.delete(result);
		
		resList = scenarioRepository.findByName(testScenName);
		assertTrue(resList.isEmpty());
	}
	
	@Test
	@Rollback(true)
	public void findAllScenario() {
		List<Scenario> resList = scenarioRepository.findAll();
		
		assertFalse(resList.isEmpty());
	}
}
