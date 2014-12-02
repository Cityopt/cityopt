package com.cityopt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

import com.cityopt.model.Project;
import com.cityopt.repository.ProjectRepository;
import com.cityopt.repository.UserGroupProjectRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
public class ProjectRepositoryTest {

	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired 
	UserGroupProjectRepository userGroupProjectRepository;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {	
		
	}

	@After
	public void tearDown() throws Exception {	
	
		
	}
	
	
	@Test	
	@Rollback(true)
	public void CreateProject()
	{	
		Project project = new Project();
		project.setName("Project 2");
		project.setLocation("Vienna");
		
		int id = project.getPrjid();
		
		projectRepository.saveAndFlush(project);
		
		assertNotEquals(id,project.getPrjid());	
	}
	
	@Test
	@Rollback(true)
	public void UpdateProject()
	{	
		String location;
		List<Project> projects = projectRepository.findByName("Project 1");
		
		Project fproject = projects.get(0);
		location = fproject.getLocation();
		fproject.setLocation("Graz");
		
		assertNotEquals(location,fproject.getLocation());	
	}	
	
		
	@Test
	public void findAll() {
	
		List<Project> projects = projectRepository.findAll();
		assertEquals(2, projects.size());	
	}
	
	@Test
	public void findByName() {
	
		List<Project> projects = projectRepository.findByName("Project 1");
		assertEquals(1, projects.size());	
		
		projects = projectRepository.findByName("Project");
		assertEquals(2, projects.size());
		
		projects = projectRepository.findByName("Projedsct");
		assertEquals(0, projects.size());
	}
	
	@Test
	@Rollback(true)
	public void DeleteProject()
	{		
		List<Project> projects = projectRepository.findByName("Project 3");
		
		projectRepository.delete(projects);
				
		assertEquals(0, projectRepository.findByName("Project 3").size());	
	}	

}
