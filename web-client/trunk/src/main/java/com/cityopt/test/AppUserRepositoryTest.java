package com.cityopt.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cityopt.model.AppUser;
import com.cityopt.repository.AppUserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-context.xml" })
@Transactional
public class AppUserRepositoryTest {

	@Autowired
	AppUserRepository appuserRepository;
	
	@Before
	public void setUp() throws Exception {
		appuserRepository.deleteAll();
		AppUser user = new AppUser();
		user.setName("Michael");
		user.setPassword("s1cher");
		appuserRepository.saveAndFlush(user);
	}

	@Test
	@Rollback(true)
	public void findByName() {
		List<AppUser> result = appuserRepository.findByUserName("Michael");
		
		assertFalse(result.isEmpty());
	}

}
