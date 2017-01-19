package eu.cityopt.service;

import static org.junit.Assert.*;

import java.io.File;
import java.text.ParseException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptException;
import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.xml.sax.InputSource;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptSearchConst;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.model.Type;
import eu.cityopt.model.Unit;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.repository.OptSearchConstRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.service.impl.DatabaseSearchOptimizationServiceImpl;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.service.OptimisationSupport;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.sim.service.SimulationService.MetricUpdateStatus;
//import eu.cityopt.sim.eval.ConstraintExpression;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/Sample Test case - SC1.xml", "classpath:/testData/Sample Test case - SC2.xml",
	"classpath:/testData/Sample Test case - SC3.xml", "classpath:/testData/Sample Test case - SC4.xml",
	"classpath:/testData/Sample Test case - SC5.xml"})
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
//@Ignore
public class DatabaseSearchOptimizationTest {

	@Autowired ComponentRepository componentRepository;
	@Autowired ProjectRepository projectRepository;
	@Autowired SimulationResultRepository simResRepository;
	@Autowired SimulationService simulationService;
	@Autowired ScenarioRepository scenarioRepository;
	@Autowired TimeSeriesValRepository timeSeriesValRepository;
	@Autowired OptimizationSetRepository optimizationSetRepository;
	@Autowired ObjectiveFunctionRepository objectiveFunctionRepository;
	@Autowired TimeSeriesRepository timeSeriesRepository;
	@Autowired TypeRepository typeRepository;
	@Autowired UnitRepository unitRepository;
	@Autowired OptimisationSupport optSupport;
	@Autowired OptSearchConstRepository optSearchConstRepository;
	@Autowired OptConstraintRepository optConstraintRepository;
	@Autowired DatabaseSearchOptimizationServiceImpl dbSearchOptService;

	@PersistenceContext EntityManager em;

    @Autowired DataSource dataSource;

	public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_MODEL_FAILURE = "MODEL_FAILURE";
    public static final String STATUS_SIMULATOR_FAILURE = "SIMULATOR_FAILURE";

//    private IDatabaseTester databaseTester;

//    public static boolean isDbSetUp=false;

//    @javax.annotation.Resource
//    public PlatformTransactionManager transactionManager;

//    @BeforeClass
//    @Test
//    public void foo(){
//
//    }

    //this is not really faster and StreamingXML producer (which might be faster) throws strange "only one iterator allowed" exception
    //additionally it seems to omit the transaction management, messing up the database everytime
//    @Before
//    public void setUp() throws Exception
//    {
//    	if(!isDbSetUp){
//    		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//      	  	def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//      	  	transactionManager.getTransaction(def).setRollbackOnly();
//
//    		databaseTester = new DataSourceDatabaseTester(dataSource);
//
//	        InputSource is2 = new InputSource("./src/test/resources/testData/fullTestcaseBecauseDBUnitIsToStupidToAllowSplitting.xml");
//
//	        FlatXmlProducer prod2 = new FlatXmlProducer(is2);
//
//			IDataSet dataSet2 = new FlatXmlDataSet(prod2);
//
//
//			databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
//			databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
//
//			databaseTester.setDataSet(dataSet2);
//
//		    databaseTester.onSetup();
//		    isDbSetUp = true;
//    	}
//    }

//    @Autowired ScriptUtils scriptUtils;

//    @After
//    public void cleanDatabase() throws Exception
//    {
//		JdbcTestUtils jtu = new JdbcTestUtils();
//		ScriptUtils.executeSqlScript(dataSource.getConnection(), new FileSystemResource("./sql/CityOPT.sql"));
//
//    }

//    @BeforeClass
//    @DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
//    	"classpath:/testData/Sample Test case - SC1.xml", "classpath:/testData/Sample Test case - SC2.xml",
//    	"classpath:/testData/Sample Test case - SC3.xml", "classpath:/testData/Sample Test case - SC4.xml",
//    	"classpath:/testData/Sample Test case - SC5.xml"})
//	public static void setUpBeforeClass() throws Exception {
//
//	}

	@Test
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
	public void searchConstEval() throws Exception {
		Project project = projectRepository.findOne(1);

		//there is just one optset, so pick this one..
		OptimizationSet optimizationSet = project.getObjectivefunctions().iterator().next().getOptimizationsets().iterator().next();

		EvaluationResults er = optSupport.evaluateScenarios(
		        project.getPrjid(), optimizationSet.getOptid());

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
	public void dbSearchOptTest() throws ParseException, ScriptException,
			EntityNotFoundException {

		SearchOptimizationResults sor = dbSearchOptService.searchConstEval(1, 1,3);

		assertEquals(3,sor.resultScenarios.size());
		ScenarioWithObjFuncValueDTO scen = sor.resultScenarios.get(0);
		assertNotNull(scen);
		assertEquals(5, scen.getScenid());
	}

	@Test
	public void dbSearchOptTestNoConstraints() throws ParseException, ScriptException,
			EntityNotFoundException {
		OptimizationSet os = optimizationSetRepository.findOne(1);
		optSearchConstRepository.deleteOptConstraintsforOptSet(1);
		em.flush();

		os = optimizationSetRepository.findOne(1);
		assertTrue(os.getOptsearchconsts().size() == 0);

		SearchOptimizationResults sor = dbSearchOptService.searchConstEval(1, 1, 5);

		assertEquals(5,sor.resultScenarios.size());
		ScenarioWithObjFuncValueDTO scen = sor.resultScenarios.get(0);
		assertNotNull(scen);
		assertEquals(5, scen.getScenid());

		sor = dbSearchOptService.searchConstEval(1, 1, 3);
		assertEquals(3,sor.resultScenarios.size());
		scen = sor.resultScenarios.get(0);
		assertNotNull(scen);
		assertEquals(5, scen.getScenid());
	}

	@Test
	public void dbSearchOptTestIsMaximise() throws ParseException, ScriptException,
			EntityNotFoundException {
		long start = System.nanoTime();
		OptimizationSet os = optimizationSetRepository.findOne(1);
		ObjectiveFunction of = os.getObjectivefunction();
		of.setIsmaximise(true);
		objectiveFunctionRepository.saveAndFlush(of);

		SearchOptimizationResults sor = dbSearchOptService.searchConstEval(1, 1,3);

		System.out.printf("time in millis: " + (System.nanoTime()- start)/1000000);

//		os = optimizationSetRepository.findOne(1);
//
//		Scenario scen = os.getScenario();

		assertEquals(3,sor.resultScenarios.size());
		ScenarioWithObjFuncValueDTO scen = sor.resultScenarios.get(0);
		assertNotNull(scen);
		assertEquals(2, scen.getScenid());

//		of.setIsmaximise(false);
//		objectiveFunctionRepository.saveAndFlush(of);
	}

	@Test
	public void dbSearchOptTestNoResult() throws ParseException, ScriptException,
			EntityNotFoundException {
		long start = System.nanoTime();
		OptimizationSet os = optimizationSetRepository.findOne(1);
		ObjectiveFunction of = os.getObjectivefunction();
		of.setIsmaximise(true);
		objectiveFunctionRepository.saveAndFlush(of);
		OptConstraint oc = optConstraintRepository.findOne(1);
		assertTrue(oc.getExpression().equals("Solar_thermal_panels.collector_area"));
		oc.setLowerbound("200");
		em.flush();

		SearchOptimizationResults sor =dbSearchOptService.searchConstEval(1, 1, 5);

		System.out.printf("time in millis: " + (System.nanoTime()- start)/1000000);

//		os = optimizationSetRepository.findOne(1);
//
//		Scenario scen = os.getScenario();

		assertEquals(0,sor.resultScenarios.size());
//		assertEquals(0, scen.getScenid());
	}

}
