package eu.cityopt.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.UnitDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
public class InputParamServiceTest {
	@Autowired
	InputParameterService inputParamService;	
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws EntityNotFoundException {
		InputParameterDTO iparam = inputParamService.findByID(1);
		UnitDTO u = iparam.getUnit();
		ComponentDTO c = iparam.getComponent();
//		int uid = iparam.getUnitID();
//		int cid = iparam.getComponentID();
		
		assertEquals(u.getName(), "myUnit");
		assertEquals(c.getName(), "myComponent");
//		assertTrue(uid == 1);
//		assertTrue(cid == 1);
		
	}

}
