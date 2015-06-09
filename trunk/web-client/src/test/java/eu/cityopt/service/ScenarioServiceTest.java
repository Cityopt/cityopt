package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioMetricsDTO;
import eu.cityopt.DTO.SimulationResultDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class ScenarioServiceTest {

	@Autowired
	ProjectService projectService;
	
	@Autowired
	ScenarioService scenarioService;
	
	@PersistenceContext
	EntityManager em;
	
	@Test(expected=EntityNotFoundException.class)
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
		"classpath:/testData/Sample Test case - SC1.xml"})
	public void deleteScenario() throws EntityNotFoundException {
		try{
			scenarioService.delete(1);
		}catch(Exception ex){
			fail("id 1 not found, check testdata");
		}
		em.flush();
		
		ScenarioDTO scen = scenarioService.findByID(1);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
		"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml"})
	public void getScenarioMetrics() throws EntityNotFoundException {
		Set<ScenarioMetricsDTO> scenmetL = scenarioService.getScenarioMetrics(1);
		assertNotNull(scenmetL);
		assertEquals(1,scenmetL.size());
		ScenarioMetricsDTO scenmet = scenmetL.iterator().next();
		ScenarioDTO scen = scenmet.getScenario();
		assertNotNull(scen);
		assertEquals(1,scen.getScenid());
		assertEquals("Sample Test case - SC1",scen.getName());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
		"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml",
		"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC2.xml",
		"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC3.xml"})
	public void findByName() throws EntityNotFoundException {
		List<ScenarioDTO> scen = scenarioService.findByName("sam");
		assertNotNull(scen);
		assertEquals(3, scen.size());
		scen = scenarioService.findByName("sc1");
		assertNotNull(scen);
		assertEquals(1, scen.size());
		scen = scenarioService.findByName("scen sc1");
		assertNotNull(scen);
		assertEquals(0, scen.size());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
		"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml",
		"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC2.xml"})
	public void getMetricsValues() throws EntityNotFoundException {
		Set<MetricValDTO> mvL = scenarioService.getMetricsValues(1);
		assertEquals(5, mvL.size());
		Optional<MetricValDTO> mvOpt = mvL.stream().filter(m -> m.getValue().equals("569399.35")).findFirst();
		assertTrue(mvOpt.isPresent());
		MetricValDTO mv = mvOpt.get();
		ScenarioMetricsDTO metScenMet = mv.getScenariometrics();
		assertEquals(1,metScenMet.getScenario().getScenid());
	}
}
