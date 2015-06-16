package eu.cityopt.service;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.repository.CustomQueryRepositoryImpl;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.sim.service.SimulationService;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class CopyServiceTest {
	
	@Autowired ScenarioRepository scenarioRepository;
	@Autowired CustomQueryRepositoryImpl customQueryRepository;
	
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
	@Autowired DecisionVariableService decisionVarService;	
	
	@PersistenceContext
	private EntityManager em;
	
	
	@Test
//	@Rollback(false)
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
		Set<InputParameterDTO> ipSet = componentService.getInputParameters(comps[0].getComponentid());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("collector_area")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("Heat_loss_coefficient")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("Temperature_dependence_of_the_heat_losses")).count());
		assertEquals(1, ipSet.stream().filter(i -> i.getName().equals("zero_loss_coefficient_for_total_or_global_radiation_at_normal_incidence")).count());
		
	
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
		
		assertEquals(projectService.findByNameContaining("copy of").size(),1);
	}	
	
	@Test
//	@Rollback(false)
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
		
		assertEquals(scenarioService.findByName("copy of").size(),1);
	}	

	@Test
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
}
