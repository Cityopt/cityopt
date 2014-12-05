package eu.cityopt.repository;

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
@ContextConfiguration(locations = { "classpath:/test-context.xml" })
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

//		userGroupProjectRepository.deleteAll();
//				
//		userGroupRepository.deleteAll();
//		
//		userRepository.deleteAll();
//		
//		projectRepository.deleteAll();
//		
//		Project project = new Project();
//		project.setName("My test project");
//		
//		projectRepository.save(project);
//		
//		UserGroup usergroup = new UserGroup();
//		usergroup.setName("Administrator");
//		
//		userGroupRepository.save(usergroup);
//		
//		AppUser appuser = new AppUser();
//		appuser.setName("Flo");		
//		
//		userRepository.save(appuser);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	public void CreateUserGroupRelation() {
		
		List<Project> projects = projectRepository.findByName("My test project");
		List<UserGroup> groups = userGroupRepository.findByGroupName("Administrator");
		List<AppUser> user = userRepository.findByUserName("Flo");
		
		UserGroupProject usergroupproject = new UserGroupProject();
		
		usergroupproject.setAppuser(user.get(0));
		usergroupproject.setProject(projects.get(0));		
		usergroupproject.setUsergroup(groups.get(0));
		
		userGroupProjectRepository.saveAndFlush(usergroupproject);
		
		assertNotEquals((Integer) 0, usergroupproject.getUsergroupprojectid());
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
		
		UserGroupProject usergroupproject = new UserGroupProject();
		
		usergroupproject.setAppuser(appuser);
		usergroupproject.setProject(project);		
		usergroupproject.setUsergroup(usergroup);
		
		//Cascade is enable!
		userGroupProjectRepository.saveAndFlush(usergroupproject);
		
		assertNotEquals((Integer) 0, usergroupproject.getUsergroupprojectid());
	}
	
	@Test
	@Rollback(true)
	public void UpdateUserGroupRelation2() {
		
		List<Project> projects = projectRepository.findByName("My test project");
		List<UserGroup> groups = userGroupRepository.findByGroupName("Administrator");
		List<AppUser> user = userRepository.findByUserName("Flo");
		
		UserGroupProject usergroupproject = new UserGroupProject();
		
		usergroupproject.setAppuser(user.get(0));
		usergroupproject.setProject(projects.get(0));		
		usergroupproject.setUsergroup(groups.get(0));
		
		userGroupProjectRepository.saveAndFlush(usergroupproject);
		
		Project project = new Project();
		project.setName("New interesting project");
		
		String before = usergroupproject.getProject().getName();
		usergroupproject.setProject(project);
						
		assertNotEquals(before,usergroupproject.getProject().getName());
	}
	
	
	@Test
	@Rollback(true)
	public void DeleteUserGroupRelation2() {
		
		List<UserGroupProject> userGroupProjects = userGroupProjectRepository.findAll();
		userGroupProjectRepository.delete(userGroupProjects);
		
		assertEquals(0, userGroupProjectRepository.findAll().size());
	}
	
	
	

}
