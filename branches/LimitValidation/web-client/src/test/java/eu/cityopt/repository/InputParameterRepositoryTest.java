package eu.cityopt.repository;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.hibernate.internal.util.SerializationHelper;
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

import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.InputParameter;
import eu.cityopt.repository.InputParameterRepository;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:jpaContext.xml", "classpath:test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
public class InputParameterRepositoryTest {
		
	@Autowired
	InputParameterRepository inParamRepository;
	
	@Autowired
	ExtParamValSetRepository extParamValSetRepository;
	
	@Before
	public void setUp() throws Exception {

	}
	
	@Test
	@Rollback(true)
	public void testFindAll() {
		List<InputParameter> result = inParamRepository.findAll();
		
		assertFalse(result.isEmpty());
		assertTrue(1 < result.size());
	}
	


}
