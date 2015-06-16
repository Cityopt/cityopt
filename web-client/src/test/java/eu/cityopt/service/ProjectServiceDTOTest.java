package eu.cityopt.service;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;
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
import org.springframework.util.Assert;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.SimulationModelDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ScenarioRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class ProjectServiceDTOTest {

	@Autowired
	ProjectService projectService;
	
	@Autowired
	SimulationModelService simulationModelService;
	
	@Autowired
	ScenarioService scenarioService;
	
	@PersistenceContext
	EntityManager em;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void findAllEmpty() {
		List<ProjectDTO> list = projectService.findAll();
		Assert.notNull(list);
		assertTrue(list.size() == 0);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void findAll() {
		List<ProjectDTO> list = projectService.findAll();
		Assert.notNull(list);
		assertTrue(list.size() > 0);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void findByNameTest() {
		List<ProjectDTO> list = projectService.findByNameContaining("project");
		Assert.notNull(list);
		assertTrue(list.size() == 1);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void findByNameTest2() {
		List<ProjectDTO> list = projectService.findByNameContaining("notAProjectName");
		Assert.notNull(list);
		assertTrue(list.size() == 0);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void getScenarios() throws EntityNotFoundException {		
		//Scenarios are not loaded with the ProjectDTO object 
		//if needed, load them from the service using the project's ID
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		Set<ScenarioDTO> scenarios2 = projectService.getScenarios(item.getPrjid());
		Assert.notNull(scenarios2);
		ScenarioDTO element2 = scenarios2.iterator().next();
		assertEquals("this is a test", element2.getDescription());
		assertEquals("test", element2.getName());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void getComponents() throws EntityNotFoundException {		
		//Scenarios are not loaded with the ProjectDTO object 
		//if needed, load them from the service using the project's ID
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		List<ComponentDTO> components = projectService.getComponents(item.getPrjid());
		Assert.notNull(components);

		ComponentDTO comp = components.stream().filter(c -> c.getName().equals("testcomponent 1"))
				.findFirst().get();
		
		assertNotNull(comp);
		assertEquals(comp.getName(), "testcomponent 1");
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void updateProject() throws EntityNotFoundException {		
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		item.setName("new project name");
		item.setDescription("new project description");
		
		projectService.update(item, projectService.getSimulationmodelId(item.getPrjid()), projectService.getDefaultExtParamSetId(item.getPrjid()));
		ProjectDTO item2 = projectService.findByID(1);
		assertNotNull(item2);
		assertEquals("new project name", item2.getName());
		assertEquals("new project description", item2.getDescription());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void getMetrics() throws EntityNotFoundException {		
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		Set<MetricDTO> metrics = projectService.getMetrics(item.getPrjid());
		Assert.notNull(metrics);
		MetricDTO met = metrics.iterator().next();
		assertEquals("myMetric", met.getName());
		assertEquals("my expression != bad", met.getExpression()); 
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void getExtParams() throws EntityNotFoundException {		
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		Set<ExtParamDTO> externals = projectService.getExtParams(item.getPrjid());
		Assert.notNull(externals);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void getExtParamValSets() {
		List<ExtParamValSetDTO> list = projectService.getExtParamValSets(1);
		Assert.notNull(list);
		assertTrue(list.size() == 1);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void getSimmulationModel() throws EntityNotFoundException {		
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		Set<ExtParamDTO> metrics = projectService.getExtParams(item.getPrjid());
		Assert.notNull(metrics);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void getProjectScenariosDTO() {	
		//Scenarios are directly loaded with the projectScenarioDTO object
//		ProjectScenariosDTO item = projectService.findAllWithScenarios().get(0);
		
		List<ProjectScenariosDTO> itms = projectService.findAllWithScenarios();
		ProjectScenariosDTO item = itms.stream().
				filter(p -> p.getPrjid() == 1).findFirst().get();
		
		
		Assert.notNull(item);
		Set<ScenarioDTO> scenarios = item.getScenarios();
		Assert.notNull(scenarios);
		ScenarioDTO element = scenarios.iterator().next();
		assertEquals("this is a test", element.getDescription());
		assertEquals("test", element.getName());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void setScenariosOnProjectTest() throws EntityNotFoundException {	
		//Scenarios are directly loaded with the projectScenarioDTO object
		ProjectDTO item = projectService.findAllWithScenarios().get(0);		
		Assert.notNull(item);
		Set<ScenarioDTO> scenarios = projectService.getScenarios(item.getPrjid());
		Assert.notNull(scenarios);
		int sizeBefore = scenarios.size();
		
		ScenarioDTO newScen = new ScenarioDTO();
		newScen.setName("My new Scenario");
		newScen.setDescription("this is my new Scenario");
		newScen = scenarioService.save(newScen, item.getPrjid());
		scenarios.add(newScen);
		
		projectService.setScenarios(item.getPrjid(), scenarios);
		item = projectService.findByID(item.getPrjid());		
		Assert.notNull(item);
		scenarios = projectService.getScenarios(item.getPrjid());
		int sizeAfter = scenarios.size();
		
		newScen.setScenid(0);
		assertEquals(sizeBefore +1, sizeAfter);
		//assertTrue(scenarios.contains(newScen));		
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void setScenarioOnScenarioServiceTest() {	
		//Scenarios are directly loaded with the projectScenarioDTO object
		ProjectScenariosDTO item = projectService.findAllWithScenarios().get(0);		
		Assert.notNull(item);
		Set<ScenarioDTO> scenarios = item.getScenarios();
		Assert.notNull(scenarios);
		int sizeBefore = scenarios.size();
		
		ScenarioDTO newScen = new ScenarioDTO();
		newScen.setName("My new Scenario");
		newScen.setDescription("this is my new Scenario");
		
		newScen = scenarioService.save(newScen, item.getPrjid());

		em.flush();
		em.clear();
		ProjectScenariosDTO item2 = projectService.findAllWithScenarios().get(0);		
		Assert.notNull(item2);
		Set<ScenarioDTO> scenarios2 = item2.getScenarios();
		Assert.notNull(scenarios2);
		int sizeAfter = scenarios2.size();
		
		Set<ScenarioDTO> testt = projectService.getScenarios(item.getPrjid());
		sizeAfter = testt.size();
		
		assertEquals(sizeBefore +1, sizeAfter);
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void CreateProjectWithSimulationModel() throws IOException, EntityNotFoundException {
		
		ProjectDTO project_2 = new ProjectDTO();
		project_2.setName("Project 2");
		project_2.setLocation("Graz");		
		
		File tmpModel = File.createTempFile("simModel",".txt");
		FileUtils.writeStringToFile(tmpModel, "Hello File");		 
		
		byte[] tmpModelarr = getFileBytes(tmpModel);
		
		SimulationModelDTO model = new SimulationModelDTO();
		model.setModelblob(tmpModelarr);
		model.setSimulator("APROS");
		model.setDescription("My second model");				
		model = simulationModelService.save(model);
//		project_2.setSimulationmodel(model);
		
		project_2 = projectService.save(project_2, model.getModelid(), 0);
		
		ProjectDTO fproject = projectService.findByID(project_2.getPrjid());
		int modactID = projectService.getSimulationmodelId(project_2.getPrjid());
		SimulationModelDTO modact = simulationModelService.findByID(modactID);
		String mydesc = modact.getDescription();
		
		assertEquals("My second model", modact.getDescription());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/scenario_TestData.xml"})
	public void getProjectSimulationmodel() throws EntityNotFoundException{
		ProjectDTO item = projectService.findByID(1);
		Assert.notNull(item);
		
		int modelId = projectService.getSimulationmodelId(item.getPrjid());
		SimulationModelDTO model = simulationModelService.findByID(modelId);
		Assert.notNull(model);
		
		byte[] tmpModelarr = model.getModelblob();	
		String content = new String(tmpModelarr, StandardCharsets.UTF_8);
		assertEquals("this is a test file for dbunit blob data.",content);
		assertEquals(model.getDescription(),"model with Base64");
		assertEquals(model.getSimulator(),"APROS");
	}
	
	
	public static byte[] getFileBytes(File file) throws IOException {
	    ByteArrayOutputStream ous = null;
	    InputStream ios = null;
	    try {
	        byte[] buffer = new byte[4096];
	        ous = new ByteArrayOutputStream();
	        ios = new FileInputStream(file);
	        int read = 0;
	        while ((read = ios.read(buffer)) != -1)
	            ous.write(buffer, 0, read);
	    } finally {
	        try {
	            if (ous != null)
	                ous.close();
	        } catch (IOException e) {
	            // swallow, since not that important
	        }
	        try {
	            if (ios != null)
	                ios.close();
	        } catch (IOException e) {
	            // swallow, since not that important
	        }
	    }
	    return ous.toByteArray();
	}
	
	@Test
//	@Rollback(false)
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void getSearchAndGAOptimizationSetsTest() throws EntityNotFoundException{
		Set<OpenOptimizationSetDTO> oosDTOs = projectService.getSearchAndGAOptimizationSets(1);
		
		for(OpenOptimizationSetDTO oosd : oosDTOs){
			System.out.println(oosd.getId() + " | " + oosd.getName() + " | " + oosd.getOptSetType());
		}
		
		assertEquals(2, oosDTOs.size());
	}
	
	@Test(expected=EntityNotFoundException.class)
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
		"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml",
		"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC2.xml"})
	public void deleteProject() throws EntityNotFoundException {
		try{
			projectService.delete(1);
		}catch(Exception ex){
			fail("id 1 not found, check testdata");
		}
		em.flush();
		
		ProjectDTO p = projectService.findByID(1);
	}
	
}
