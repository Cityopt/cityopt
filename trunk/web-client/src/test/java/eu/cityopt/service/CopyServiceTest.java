package eu.cityopt.service;

import static org.junit.Assert.*;

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

import eu.cityopt.repository.ScenarioRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class CopyServiceTest {
	
	@Autowired CopyService copyService;
	@Autowired ProjectService projectService;
	@Autowired ScenarioService scenarioService;
	@Autowired ScenarioRepository scenarioRepository;
	
	@Test
//	@Rollback(false)
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void copyProject() throws EntityNotFoundException{

		try {
			copyService.copyProject(1, "copy of p1");
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(projectService.findByName("copy of").size(),1);
	}
	
	@Test
//	@Rollback(false)
	@DatabaseSetup({"classpath:/testData/plumbing_scengen.xml"})
	public void copyProject2() throws EntityNotFoundException{

		try {
			copyService.copyProject(1, "copy of p1");
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(projectService.findByName("copy of").size(),1);
	}	
	
	@Test
//	@Rollback(false)
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
		 "classpath:/testData/Sample Test case - SC1.xml"})
	public void copyScenario() throws EntityNotFoundException{
		try {
			copyService.copyScenario(1, "copy of s1", true, true, true, true);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(scenarioService.findByName("copy of").size(),1);
	}	

}
