package eu.cityopt.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.repository.CustomQueryRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.sim.service.SimulationService;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class CopyServiceTest {
	
	@Autowired ScenarioRepository scenarioRepository;
	@Autowired CustomQueryRepository customQueryRepository;
	
	@Autowired ProjectService projectService; 	
	@Autowired ScenarioService scenarioService;	
	@Autowired AppUserService userService;	
	@Autowired ComponentService componentService;
	@Autowired ComponentInputParamDTOService componentInputParamService;	
	@Autowired InputParameterService inputParamService;
	@Autowired InputParamValService inputParamValService;
	@Autowired ExtParamService extParamService;
	@Autowired ExtParamValService extParamValService;
	@Autowired ExtParamValSetService extParamValSetService;	
	@Autowired MetricService metricService;
	@Autowired MetricValService metricValService;
	@Autowired UnitService unitService;	
	@Autowired SimulationService simService;
	@Autowired SimulationResultService simResultService;
	@Autowired TimeSeriesService timeSeriesService;
	@Autowired OutputVariableService outputVarService;	
	@Autowired TypeService typeService;
	@Autowired CopyService copyService;	
	@Autowired OptimizationSetService optSetService;	
	@Autowired ObjectiveFunctionService objFuncService;	
	@Autowired OptConstraintService optConstraintService;	
	@Autowired OptSearchConstService optSearchService;	
	@Autowired ScenarioGeneratorService scenGenService;	
	@Autowired ScenarioGeneratorRepository scenarioGeneratorRepository;
	@Autowired DecisionVariableService decisionVarService;	
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired DataSource ds;
	
	@Before
	public void setUp(){
		
	}
	
	@After
	public void tearDown(){
		
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	 "classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml",
	 "classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC2.xml"})
	 @ExpectedDatabase(value="classpath:/testData/project1TestData_projectCopyResult.xml",
     assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@DirtiesContext
	//for some reason the use of @ExpectedDatabase annotation prevents rolling back the transaction. 
	//Therefore @DirtiesContext is used, to reload the context after the test case which ensures a clear state 
	public void copyProject3() throws EntityNotFoundException, SQLException{
		customQueryRepository.updateSequences();
		try {
			copyService.copyProject(1, "copy of Sample Project");
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		em.clear();
		
		List<ProjectDTO> pList = projectService.findByNameContaining("copy of");
		
		assertEquals(1, pList.size());
		ProjectDTO p = pList.get(0);
		
		List<ComponentDTO> compList = projectService.getComponents(p.getPrjid()); 
		assertEquals(3, compList.size());
		ComponentDTO[] comps = compList.stream().filter(c -> c.getName().equals("Solar_thermal_panels")).toArray(size -> new ComponentDTO[size]);
		assertNotNull(comps);
		List<InputParameterDTO> ipSet = componentService.getInputParameters(comps[0].getComponentid());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("collector_area")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("Heat_loss_coefficient")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("Temperature_dependence_of_the_heat_losses")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("zero_loss_coefficient_for_total_or_global_radiation_at_normal_incidence")).count());
	
	}
	
	@Test
	@DirtiesContext
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	 "classpath:/testData/Sample Test case - SC1.xml"})
	public void copyProject() throws EntityNotFoundException, SQLException{
		customQueryRepository.updateSequences();
		try {
			copyService.copyProject(1, "copy of p1");
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		em.clear();
		
		List<ProjectDTO> pList = projectService.findByNameContaining("copy of");
		
		assertEquals(1, pList.size());
		ProjectDTO p = pList.get(0);
		
		List<ComponentDTO> compList = projectService.getComponents(p.getPrjid()); 
		assertEquals(3, compList.size());
		ComponentDTO[] comps = compList.stream().filter(c -> c.getName().equals("Solar_thermal_panels")).toArray(size -> new ComponentDTO[size]);
		assertNotNull(comps);
		List<InputParameterDTO> ipSet = componentService.getInputParameters(comps[0].getComponentid());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("collector_area")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("Heat_loss_coefficient")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("Temperature_dependence_of_the_heat_losses")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("zero_loss_coefficient_for_total_or_global_radiation_at_normal_incidence")).count());
	
	}
	
	@Test
	@DirtiesContext
	@DatabaseSetup({"classpath:/testData/plumbing_scengen.xml"})
	public void copyProject2() throws EntityNotFoundException{

		try {
			copyService.copyProject(1, "copy of p1");
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(projectService.findByNameContaining("copy of").size(),1);
	}	
	
	@Test
	@DirtiesContext
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
		 "classpath:/testData/Sample Test case - SC1.xml"})
	public void copyScenario() throws EntityNotFoundException, SQLException{
		customQueryRepository.updateSequences();
		try {
			copyService.copyScenario(1, "copy of s1", true, true, true, true);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(scenarioService.findByNameContaining("copy of").size(),1);
	}	

	@Test
	@DirtiesContext
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void copyMetric() throws EntityNotFoundException{

		List<MetricDTO> metricsBefore = metricService.findAll();
		
		String metric1 = "copy of Energy_Supply2";
		String metric2 = "copy of copy of Energy_Supply2";
		String metric3 = "copy of Cost";
		
		MetricDTO metricid1Before = metricService.findByID(3);
		MetricDTO metricid6Before = metricService.findByID(6);
		
		copyService.copyMetric(3, metric1);
		copyService.copyMetric(3, metric2);
		copyService.copyMetric(6, metric3);
		
		em.flush();
		em.clear();
		
		List<MetricDTO> metricsAfter = metricService.findAll();
		
		assertEquals(metricsBefore.size()+3, metricsAfter.size());
		assertEquals(1, metricsAfter.stream().filter(m -> m.getName().equals(metric1)).count());
		assertEquals(1, metricsAfter.stream().filter(m -> m.getName().equals(metric2)).count());
		assertEquals(1, metricsAfter.stream().filter(m -> m.getName().equals(metric3)).count());
		MetricDTO metricid1After = metricsAfter.stream().filter(m -> m.getName().equals(metric1)).findFirst().get();
		MetricDTO metricid6After = metricsAfter.stream().filter(m -> m.getName().equals(metric3)).findFirst().get();
		assertEquals(metricid1Before.getExpression(), metricid1After.getExpression());
		assertEquals(metricid6Before.getExpression(), metricid6After.getExpression());
	}
	
	@Test
	@DirtiesContext
	@DatabaseSetup("classpath:/testData/plumbing_scengen_copy.xml")
    @ExpectedDatabase(value="classpath:/testData/plumbing_scengen_scenGenCopyResult.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void copyScengen() throws EntityNotFoundException, SQLException{
		customQueryRepository.updateSequences();
		ScenarioGeneratorDTO sgd = copyService.copyScenarioGenerator(1, "copy of scengentest");
		em.flush();
		em.clear();
	}
	
	@Test
	@DirtiesContext
	@DatabaseSetup({"classpath:/testData/globalTestData.xml","classpath:/testData/project1TestData.xml"})
    @ExpectedDatabase(value="classpath:/testData/project1TestData_optSetCopyResult.xml",
        assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void copyOptSet() throws EntityNotFoundException, SQLException{
		customQueryRepository.updateSequences();
		copyService.copyOptimizationSet(1, "copy of Optimization Set 1", false);
		em.flush();
		em.clear();
	}
}
