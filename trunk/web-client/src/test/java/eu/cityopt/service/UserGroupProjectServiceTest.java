
package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.UserGroupDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.service.impl.GrantedAuthorityImpl;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/appUser_TestData.xml"})
public class UserGroupProjectServiceTest {

	@Autowired
	UserGroupProjectService userGroupProjectService;
	
	@Autowired
	AppUserService userService;
	
	@PersistenceContext
	EntityManager em;
	
	@Test
	public void findAllTest() {
		List<UserGroupProjectDTO> ugpList = userGroupProjectService.findAll();
		assertNotNull(ugpList);
		assertEquals(5, ugpList.size());
		assertEquals(2, ugpList.stream().filter(u -> u.getUsergroup().getName().equals("ROLE_Administrator")).count());
	}
	
	@Test
	public void findByUserId() {
		List<UserGroupProjectDTO> ugpList = userGroupProjectService.findByUser(1);
		assertNotNull(ugpList);
		assertEquals(2, ugpList.size());
		for(UserGroupProjectDTO ugp : ugpList)
			assertTrue(ugp.getAppuser().getName().equals("Michael"));
	}
	
	@Test
	public void findByUserName() throws EntityNotFoundException {
		AppUserDTO appUser_1 = userService.findByName("Michael");
		assertNotNull(appUser_1);
		
		AppUserDTO appUser_2 = userService.findByName("Michael Test");
		assertNull(appUser_2);
	}
	
	@Test
	public void findUserDetails() {
		UserDetails details = userGroupProjectService.findUserDetails("Michael");
		
		assertNotNull(details);
		assertEquals(details.getAuthorities().size(),2);
		
		GrantedAuthority[] grantedAuthorities = (GrantedAuthority[])details.getAuthorities().toArray(new GrantedAuthority[details.getAuthorities().size()]);
		
		assertEquals("ROLE_Administrator",grantedAuthorities[0].getAuthority());
		assertEquals("ROLE_Reader",grantedAuthorities[1].getAuthority());				
	}
	
	@Test
	public void findByGroup() {
		List<UserGroupProjectDTO> ugpList = userGroupProjectService.findByGroup(1);
		assertNotNull(ugpList);
		assertEquals(2, ugpList.size());
		ugpList = userGroupProjectService.findByGroup(2);
		assertNotNull(ugpList);
		assertEquals(0, ugpList.size());
		ugpList = userGroupProjectService.findByGroup(3);
		assertNotNull(ugpList);
		assertEquals(3, ugpList.size());
		assertEquals(3, ugpList.stream().filter(u -> u.getUsergroup().getName().equals("ROLE_Reader")).count());
	}

	@Test
	public void findByGroup2() {
		List<UserGroupProjectDTO> ugpList = userGroupProjectService.findByGroup(34);
		assertNotNull(ugpList);
		assertEquals(0, ugpList.size());
	}
	
	@Test
	public void findByProject() {
		List<UserGroupProjectDTO> ugpList = userGroupProjectService.findByProject(1);
		assertNotNull(ugpList);
		assertEquals(3, ugpList.size());
		ugpList = userGroupProjectService.findByProject(2);
		assertNotNull(ugpList);
		assertEquals(2, ugpList.size());
		ugpList = userGroupProjectService.findByProject(234);
		assertNotNull(ugpList);
		assertEquals(0, ugpList.size());
	}

	@Test
	public void findByUserAndProject() {
		UserGroupProjectDTO ugp_1 = userGroupProjectService.findByUserAndProject(1, null);
		assertNotNull(ugp_1);
		assertEquals(1, ugp_1.getUsergroup().getUsergroupid());
		
		UserGroupProjectDTO ugp_2 = userGroupProjectService.findByUserAndProject(1, 2);
		assertNotNull(ugp_2);
		assertEquals(3, ugp_2.getUsergroup().getUsergroupid());		
	}
	
	@Test
	public void findUserOfProject() {
		
		List<AppUserDTO> listUser_1 = userGroupProjectService.findUsersOfProject(1);
		assertEquals(2,listUser_1.size());
		
		List<AppUserDTO> listUser_2 = userGroupProjectService.findUsersOfProject(100);
		assertNull(listUser_2);
	}
	
	@Test
	public void findProjectsByUser() {
		
		UserGroupDTO userGroupReader = new UserGroupDTO();
		userGroupReader.setName("ROLE_Reader");
		
		UserGroupDTO userGroupExpert = new UserGroupDTO();
		userGroupExpert.setName("ROLE_Expert");
		
		List<ProjectDTO> listProjects_1 = userGroupProjectService.findProjectsByUser(2);
		assertEquals(2, listProjects_1.size());
		
		List<ProjectDTO> listProjects_2 = userGroupProjectService.findProjectsByUser(2,userGroupReader);
		assertEquals(2, listProjects_2.size());
		
		listProjects_2 = userGroupProjectService.findProjectsByUser(2,userGroupExpert);
		assertNull(listProjects_2);				
	}
	
	@Test
	public void addUserToGroupAndProject() throws EntityNotFoundException {
		userService.addToUserGroupProject(4, 1, 2);
		UserGroupProjectDTO ugp = userGroupProjectService.findByUserAndProject(4, 2);
		assertNotNull(ugp);
		assertEquals(1, ugp.getUsergroup().getUsergroupid());
	}
}
