package eu.cityopt.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.util.Set;

import javax.script.ScriptException;

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

import eu.cityopt.model.MetricVal;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.service.impl.DatabaseSearchOptimizationServiceImpl;
import eu.cityopt.sim.service.OptimisationSupport;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.sim.service.SimulationService.MetricUpdateStatus;
//import eu.cityopt.sim.eval.ConstraintExpression;

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
public class DatabaseSearchOptimizationTest {
	
	@Autowired
	ComponentRepository componentRepository;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	SimulationResultRepository simResRepository;
	
	@Autowired
	SimulationService simulationService;
	
	@Autowired
	ScenarioRepository scenarioRepository;
	
	@Autowired
	TimeSeriesValRepository timeSeriesValRepository;
	
	@Autowired
	OptimizationSetRepository optimizationSetRepository;
	
	@Autowired
	ObjectiveFunctionRepository objectiveFunctionRepository;
	
	@Autowired
	TimeSeriesRepository timeSeriesRepository;
	
	@Autowired
	OptimisationSupport optSupport;
	
	@Autowired
	DatabaseSearchOptimizationServiceImpl dbSearchOptService;

	public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_MODEL_FAILURE = "MODEL_FAILURE";
    public static final String STATUS_SIMULATOR_FAILURE = "SIMULATOR_FAILURE";
	
	@Test
	@Rollback
	@Transactional
	public void testMetricCalculation() throws Exception{
		
		Project project = projectRepository.findOne(1);		
        
        MetricUpdateStatus status = simulationService.updateMetricValues(project.getPrjid(), 1);
        
        Set<Integer> mapp = status.updated;
        
        System.out.println(status);
        for(int scenID : mapp){
        	for (ScenarioMetrics sm : scenarioRepository.findOne(scenID).getScenariometricses()){
        		for(MetricVal mv : sm.getMetricvals()){
        			System.out.println(mv.getMetric().getName());
        			System.out.println(mv.getValue());
        		}
        	}
        }
	}
	
	@Test
	@Transactional
	public void searchConstEval() throws ParseException, ScriptException{   	
		Project project = projectRepository.findOne(1);
		
		//there is just one optset, so pick this one..
		OptimizationSet optimizationSet = project.getObjectivefunctions().iterator().next().getOptimizationsets().iterator().next();
		
		EvaluationResults er = optSupport.evaluateScenarios(project, optimizationSet);
		
		System.out.println(er);
		
		assertEquals(3, er.feasible.size());
		assertEquals(2, er.infeasible.size());
		
		for (int id : er.infeasible){
			System.out.println("infeasible id: " + id);
		}
		
		//scenid 3 + 4 are infeasible?
		assertTrue(er.infeasible.contains(3));
		assertTrue(er.infeasible.contains(4));
	}
	
	@Test
	@Transactional
	public void dbSearchOptTest() throws ParseException, ScriptException, 
			EntityNotFoundException {	
		
		dbSearchOptService.searchConstEval(1, 1);
		
		OptimizationSet os = optimizationSetRepository.findOne(1);
		
		Scenario scen = os.getScenario();
		
		assertNotNull(scen);
		assertEquals(5, scen.getScenid());
	}
	
	@Test
	@Rollback
	@Transactional
	public void dbSearchOptTestIsMaximise() throws ParseException, ScriptException, 
			EntityNotFoundException {   
		long start = System.nanoTime();
		OptimizationSet os = optimizationSetRepository.findOne(1);
		ObjectiveFunction of = os.getObjectivefunction();
		of.setIsmaximise(true);
		objectiveFunctionRepository.saveAndFlush(of);		
		
		dbSearchOptService.searchConstEval(1, 1);
		
		System.out.printf("time in millis: " + (System.nanoTime()- start)/1000000);
		
		os = optimizationSetRepository.findOne(1);
		
		Scenario scen = os.getScenario();
		
		assertNotNull(scen);
		assertEquals(2, scen.getScenid());
	}
	
}
