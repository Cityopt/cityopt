package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;

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

import eu.cityopt.DTO.OptConstraintDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
public class OptimizationSetServiceTest {
	
	@Autowired
	OptimizationSetService optimizationSetService; 
	
	@Test
	public void test() throws EntityNotFoundException {
		List<OptConstraintDTO> optConst = optimizationSetService.getSearchConstraints(1);

		boolean cont1 = false;
		boolean cont2 = false;
		boolean cont3 = false;
		for(OptConstraintDTO ocd : optConst){
			if(ocd.getExpression().equals("Solar_thermal_panels.heating_power"))
				cont1=true;
			if(ocd.getExpression().equals("Storage_Vertical_tank_with_heat_structure.Height_of_tank"))
				cont2=true;
			if(ocd.getExpression().equals("Energy_Supply2"))
				cont3=true;
		}
		assertTrue(cont1 && cont2 && cont3);
		assertEquals(7, optConst.size());
	}

}
