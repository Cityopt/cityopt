package eu.cityopt.service;

import static org.junit.Assert.*;

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
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/Sample Test case - SC1.xml", "classpath:/testData/Sample Test case - SC2.xml",
	"classpath:/testData/Sample Test case - SC3.xml", "classpath:/testData/Sample Test case - SC4.xml",
	"classpath:/testData/Sample Test case - SC5.xml"})
public class ComponentServiceTest {
	@Autowired
	ComponentService componentService;	
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
//	@Rollback(false)
	public void test() throws EntityNotFoundException {
		ComponentDTO com = componentService.findByID(1);
//		UnitDTO u = iparam.getUnit();
//		ComponentDTO c = iparam.getComponent();
////		int uid = iparam.getUnitID();
//		int cid = iparam.getComponentID();
		
		assertEquals(com.getName(), "Solar_thermal_panels");
//		assertEquals(c.getName(), "myComponent");
//		assertTrue(uid == 1);
//		assertTrue(cid == 1);
		
	}

}
