package com.cityopt.test;

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

import com.cityopt.model.AppUser;
import com.cityopt.repository.AppUserRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-context.xml" })
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
	@Rollback(true)
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
	public void authenticateUser() {
		AppUser appuser = appuserRepository.authenticateUser("Michael", "s1cher");
		
		assertNotNull(appuser);
		assertNotNull(appuser.getUserid());
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
