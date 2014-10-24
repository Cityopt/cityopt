package com.cityopt.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.Project;
import com.cityopt.repository.ProjectRepository;
import com.cityopt.repository.UserGroupProjectRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test-context.xml"})
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
		
		userGroupProjectRepository.deleteAll();		
		projectRepository.deleteAll();
		
		Project project = new Project();
		project.setPrjname("Project 1");
		project.setPrjlocation("Vienna");
		
		projectRepository.saveAndFlush(project);
	}

	@After
	public void tearDown() throws Exception {	
	
		
	}
	
	
	@Test	
	@Rollback(true)
	public void CreateProject()
	{	
		Project project = new Project();
		project.setPrjname("Project 2");
		project.setPrjlocation("Vienna");
		
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
		location = fproject.getPrjlocation();
		fproject.setPrjlocation("Graz");
		
		assertNotEquals(location,fproject.getPrjlocation());	
	}	
	
		
	@Test
	public void findAll() {
	
		List<Project> projects = projectRepository.findAll();
		assertEquals(1, projects.size());	
	}
	
	@Test
	public void findByName() {
	
		List<Project> projects = projectRepository.findByName("Project 1");
		assertEquals(1, projects.size());	
		
		projects = projectRepository.findByName("Project");
		assertEquals(1, projects.size());
		
		projects = projectRepository.findByName("Projedsct");
		assertEquals(0, projects.size());
	}
	
	@Test
	@Rollback(true)
	public void DeleteProject()
	{		
		List<Project> projects = projectRepository.findByName("Project 1");
		
		projectRepository.delete(projects);
				
		assertEquals(0, projectRepository.findByName("Project 1").size());	
	}	

}
