package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.Iterator;
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

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioMetricsDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptimizationSet;

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
	
	@Autowired
	InputParameterService inputParameterService;
	
	@Autowired
	ComponentService componentService;
	
	@Autowired
	InputParamValService inputParamValService;
	
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
		List<ScenarioDTO> scen = scenarioService.findByNameContaining("sam");
		assertNotNull(scen);
		assertEquals(3, scen.size());
		scen = scenarioService.findByNameContaining("sc1");
		assertNotNull(scen);
		assertEquals(1, scen.size());
		scen = scenarioService.findByNameContaining("scen sc1");
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
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void saveWithDefaultInputs() throws EntityNotFoundException {
		
		long start = System.nanoTime();		
		
		
		ScenarioDTO scenario = new ScenarioDTO();
		scenario.setName("test with default inputs");
		scenario.getScenid();
		
		scenario = scenarioService.save(scenario, 1);
		em.flush();
		em.clear();
		
		List<ComponentDTO> components = projectService.getComponents(1);
		
		// Create input param vals for all input params
		for (int i = 0; i < components.size(); i++)
		{
			ComponentDTO component = components.get(i);
			//List<ComponentInputParamDTO> listComponentInputParams = componentInputParamService.findAllByComponentId(component.getComponentid());
			
			List<InputParameterDTO> setInputParams = componentService.getInputParameters(component.getComponentid());
			Iterator<InputParameterDTO> iter = setInputParams.iterator();

			while(iter.hasNext())
			{
				InputParameterDTO inputParam = iter.next();
				
				//InputParameterDTO inputParam = inputParamService.findByID(setInputParams.get(j).getInputid());
				InputParamValDTO inputParamVal = new InputParamValDTO();
				inputParamVal.setInputparameter(inputParam);
				inputParamVal.setValue(inputParam.getDefaultvalue());
				inputParamVal.setScenario(scenario);
				inputParamVal = inputParamValService.save(inputParamVal);
			}
		}
		
		System.out.printf("time in millis: " + (System.nanoTime()- start)/1000000);
	}
	
}
