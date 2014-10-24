package com.ait.test;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

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

import com.ait.model.Appuser;
import com.ait.model.Project;
import com.ait.model.Usergroup;
import com.ait.model.Usergroupproject;
import com.ait.repository.ProjectRepository;
import com.ait.repository.UserGroupProjectRepository;
import com.ait.repository.UserGroupRepository;
import com.ait.repository.UserRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-context.xml" })
@Transactional
public class UserGroupProjectRepositoryTest {
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	UserGroupProjectRepository userGroupProjectRepository;
	
	@Autowired
	UserGroupRepository userGroupRepository;
	
	@Autowired
	UserRepository userRepository;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		userGroupProjectRepository.deleteAll();
				
		userGroupRepository.deleteAll();
		
		userRepository.deleteAll();
		
		projectRepository.deleteAll();
		
		Project project = new Project();
		project.setPrjname("My test project");
		
		projectRepository.save(project);
		
		Usergroup usergroup = new Usergroup();
		usergroup.setUsergroupname("Administrator");
		
		userGroupRepository.save(usergroup);
		
		Appuser appuser = new Appuser();
		appuser.setUsername("Flo");		
		
		userRepository.save(appuser);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	public void CreateUserGroupRelation() {
		
		List<Project> projects = projectRepository.findByName("My test project");
		List<Usergroup> groups = userGroupRepository.findByGroupName("Administrator");
		List<Appuser> user = userRepository.findByUserName("Flo");
		
		Usergroupproject usergroupproject = new Usergroupproject();
		
		usergroupproject.setAppuser(user.get(0));
		usergroupproject.setProject(projects.get(0));		
		usergroupproject.setUsergroup(groups.get(0));
		
		userGroupProjectRepository.saveAndFlush(usergroupproject);
		
		assertNotEquals(0, usergroupproject.getUsergroupprojectid());
	}
	
	
	
	@Test
	@Rollback(true)
	public void CreateUserGroupRelation2() {
		
		Project project = new Project();
		project.setPrjname("My test project 2");
		
		projectRepository.saveAndFlush(project);
		
		Usergroup usergroup = new Usergroup();
		usergroup.setUsergroupname("Viewer");
		
		userGroupRepository.save(usergroup);
		
		Appuser appuser = new Appuser();
		appuser.setUsername("Detlef");		
		
		Usergroupproject usergroupproject = new Usergroupproject();
		
		usergroupproject.setAppuser(appuser);
		usergroupproject.setProject(project);		
		usergroupproject.setUsergroup(usergroup);
		
		//Cascade is enable!
		userGroupProjectRepository.saveAndFlush(usergroupproject);
		
		assertNotEquals(0, usergroupproject.getUsergroupprojectid());
	}
	
	@Test
	@Rollback(true)
	public void UpdateUserGroupRelation2() {
		
		List<Project> projects = projectRepository.findByName("My test project");
		List<Usergroup> groups = userGroupRepository.findByGroupName("Administrator");
		List<Appuser> user = userRepository.findByUserName("Flo");
		
		Usergroupproject usergroupproject = new Usergroupproject();
		
		usergroupproject.setAppuser(user.get(0));
		usergroupproject.setProject(projects.get(0));		
		usergroupproject.setUsergroup(groups.get(0));
		
		userGroupProjectRepository.saveAndFlush(usergroupproject);
		
		Project project = new Project();
		project.setPrjname("New interesting project");
		
		String before = usergroupproject.getProject().getPrjname();
		usergroupproject.setProject(project);
						
		assertNotEquals(before,usergroupproject.getProject().getPrjname());
	}
	
	
	@Test
	@Rollback(true)
	public void DeleteUserGroupRelation2() {
		
		List<Usergroupproject> userGroupProjects = userGroupProjectRepository.findAll();
		userGroupProjectRepository.delete(userGroupProjects);
		
		assertEquals(0, userGroupProjectRepository.findAll().size());
	}
	
	
	

}
