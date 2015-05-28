package eu.cityopt.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import eu.cityopt.DTO.ProjectDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml","classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC2.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC3.xml", "classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC4.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC5.xml"})
public class ComponentServiceTest {
	@Autowired
	ComponentService componentService;	
	
	@PersistenceContext
	EntityManager em;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
//	@Rollback(false)
	public void testFindByID() throws EntityNotFoundException {
		ComponentDTO com = componentService.findByID(1);
		
		assertEquals(com.getName(), "Solar_thermal_panels");
	}
	
	@Autowired
	ProjectService pjService;
	
	@Test
//	@Rollback(false)
//	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
//	"classpath:/testData/Sample Test case - SC1.xml"})
	public void DeleteProject() throws EntityNotFoundException
	{		
		long start= System.nanoTime();
		
		List<ProjectDTO> projects = pjService.findByName("Project");
		
		for(ProjectDTO p : projects)
			pjService.delete(p.getPrjid());
		
		long end = System.nanoTime();
		
		System.out.println("time of execution " + (end-start)/1000000);
		em.flush();
		
		assertEquals(0, pjService.findByName("Project").size());	
	}	

}
