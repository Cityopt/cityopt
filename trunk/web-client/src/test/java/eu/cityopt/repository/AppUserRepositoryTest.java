package eu.cityopt.repository;

import static org.junit.Assert.*;

import java.util.List;

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
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import eu.cityopt.model.AppUser;
import eu.cityopt.repository.AppUserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/appUser_TestData.xml")
public class AppUserRepositoryTest {

	@Autowired
	AppUserRepository appuserRepository;
	
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void create() {
		
		List<AppUser> result = appuserRepository.findByUserName("flo_new");
		assertEquals(0,result.size());
		
		AppUser user = new AppUser();
		user.setName("flo_new");
		user.setPassword("secretsecret");
		
		appuserRepository.saveAndFlush(user);
		
		result = appuserRepository.findByUserName("flo_new");
		assertEquals(1,result.size());
		
	}
	
	@Test
	public void delete() {
		List<AppUser> result = appuserRepository.findByUserName("Michael");
		
		appuserRepository.delete(result.get(0));
		result = appuserRepository.findByUserName("Michael");
		assertEquals(1, result.size());
	}

	
	@Test
	@Rollback(true)
	public void deleteNotExisting() {
		Integer sizeBefore = appuserRepository.findAll().size();
		
		AppUser toDelete = new AppUser();
		toDelete.setName("Bepi");
		toDelete.setPassword("secur1ty");
		appuserRepository.delete(toDelete);
		
		Integer sizeAfter = appuserRepository.findAll().size();
		
		assertEquals((Integer) sizeAfter, (Integer) sizeBefore);
	}
	
	@Test
	@Rollback(true)
	@ExpectedDatabase(assertionMode=DatabaseAssertionMode.NON_STRICT, value="classpath:/testData/appUser_ExpectedData.xml")
	//mode= non_strict because dbunit tries to match the number of database tables instead
	public void updateUser() {
		List<AppUser> result = appuserRepository.findByUserName("Michael2");
		appuserRepository.delete(result);
		
		result = appuserRepository.findByUserName("Flo");
		
		AppUser user = result.get(0);
		user.setName("Florian");
		user.setPassword("s3curity");
		appuserRepository.saveAndFlush(user);
	}
	
	@Test
	@Rollback(true)
	public void authenticateUser_WrongCredentials() {
		AppUser appuser = appuserRepository.authenticateUser("Michael", "s1cherhe1t");
		
		assertNull(appuser);	
	}
	
	@Test
	public void findByName() {
		List<AppUser> result = appuserRepository.findByUserName("Michael");
		
		assertEquals(2, result.size());
	}

}
