package eu.cityopt.repository;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

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

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.model.AppUser;
import eu.cityopt.model.Project;
import eu.cityopt.model.UserGroup;
import eu.cityopt.model.UserGroupProject;
import eu.cityopt.repository.AppUserRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.UserGroupProjectRepository;
import eu.cityopt.repository.UserGroupRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/appUser_TestData.xml")
public class UserGroupProjectRepositoryTest {
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	UserGroupProjectRepository userGroupProjectRepository;
	
	@Autowired
	UserGroupRepository userGroupRepository;
	
	@Autowired
	AppUserRepository userRepository;

	
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
	public void CreateUserGroupRelation() {
		
		List<Project> projects = projectRepository.findByName("Sample Project Helsinki");
		List<UserGroup> groups = userGroupRepository.findByGroupNameContaining("Administrator");
		List<AppUser> user = userRepository.findByUserName("Flo");
		
		UserGroupProject usergroupproject = new UserGroupProject();
		
		usergroupproject.setAppuser(user.get(0));
		usergroupproject.setProject(projects.get(0));		
		usergroupproject.setUsergroup(groups.get(0));
		
		usergroupproject = userGroupProjectRepository.saveAndFlush(usergroupproject);
		
		assertNotEquals((Integer) 0, usergroupproject.getUsergroupprojectid());
		assertEquals(2, userGroupProjectRepository.findByUser(3).size());
	}
	
	
	
	@Test
	@Rollback(true)
	public void CreateUserGroupRelation2() {
		
		Project project = new Project();
		project.setName("My test project 2");
		
		projectRepository.saveAndFlush(project);
		
		UserGroup usergroup = new UserGroup();
		usergroup.setName("Viewer");
		
		userGroupRepository.save(usergroup);
		
		AppUser appuser = new AppUser();
		appuser.setName("Detlef");		
		appuser = userRepository.save(appuser);
		
		UserGroupProject usergroupproject = new UserGroupProject();
		
		usergroupproject.setAppuser(appuser);
		usergroupproject.setProject(project);		
		usergroupproject.setUsergroup(usergroup);
		
		usergroupproject = userGroupProjectRepository.save(usergroupproject);
		//Cascade is enable!
		userGroupProjectRepository.saveAndFlush(usergroupproject);
		
		assertNotEquals((Integer) 0, usergroupproject.getUsergroupprojectid());
	}
	
	@Test
	@Rollback(true)
	public void UpdateUserGroupRelation2() {
		
		List<Project> projects = projectRepository.findByName("Sample Project Helsinki");
		List<UserGroup> groups = userGroupRepository.findByGroupNameContaining("Administrator");
		List<AppUser> user = userRepository.findByUserName("Flo");
		
		Optional<UserGroupProject> ugp = userGroupProjectRepository.findByUser(3).stream().filter(i -> i.getProject().getPrjid() == 1).findFirst();
		
		assertTrue(ugp.isPresent());
		UserGroupProject ugpI = ugp.get();
		ugpI.setProject(projects.get(0));
		
		userGroupProjectRepository.saveAndFlush(ugpI);
		
		ugp = userGroupProjectRepository.findByUser(3).stream().filter(i -> i.getProject().getPrjid() == 1).findFirst();
		assertFalse(ugp.isPresent());
	}
	
	
	@Test
	@Rollback(true)
	public void DeleteUserGroupRelation2() {
		
		List<UserGroupProject> userGroupProjects = userGroupProjectRepository.findAll();
		userGroupProjectRepository.delete(userGroupProjects);
		
		assertEquals(0, userGroupProjectRepository.findAll().size());
	}
	
	
	

}
