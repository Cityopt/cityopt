package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

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

import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.ProjectDTO;

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
	
	@Autowired
	ProjectService projectService; 
	
	@Test
	public void testGetSearchConstraints() throws EntityNotFoundException {
		List<OptConstraintDTO> optConst = optimizationSetService.getOptConstraints(1);

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
	
	@Test
//	@Rollback(false)
	public void testSaveOptimizationSet() throws EntityNotFoundException {
		ProjectDTO project = projectService.findByID(1);
		Integer sizeBefore = optimizationSetService.findAll().size();

		OptimizationSetDTO testSet = new OptimizationSetDTO();

		testSet.setProject(project);
		testSet.setName("my first optset");
		
		testSet = optimizationSetService.save(testSet);

//		optimizationSetService.findByID(id)
		assertEquals(sizeBefore + 1, optimizationSetService.findAll().size());
		Set<OptimizationSetDTO> pOset = projectService.getSearchOptimizationSets(1);
		assertNotNull(pOset);
		boolean found = false;
		for(OptimizationSetDTO os : pOset){
			if(testSet.getName() == os.getName())
				found = true;
		}
		assertTrue(found);
	}

	@Test
//	@Rollback(false)
	public void testUpdateOptimizationSet() throws EntityNotFoundException {
		
		OptimizationSetDTO testSet = optimizationSetService.findByID(1);
		testSet.setName("my first optset");
		testSet.getProject().setDescription("asdf");
		testSet = optimizationSetService.save(testSet);
		
		assertEquals(testSet.getName(), optimizationSetService.findByID(1).getName());
	}
}
