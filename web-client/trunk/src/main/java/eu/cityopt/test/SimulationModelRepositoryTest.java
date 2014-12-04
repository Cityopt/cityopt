package eu.cityopt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.helper.Helper;
import eu.cityopt.model.Project;
import eu.cityopt.model.SimulationModel;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.SimulationModelRepository;
import eu.cityopt.repository.UserGroupProjectRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-context.xml" })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/simulationModel_TestData.xml")
public class SimulationModelRepositoryTest {

	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	UserGroupProjectRepository userGroupProjectRepository;

	@Autowired
	SimulationModelRepository simulationModelRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
//		userGroupProjectRepository.deleteAll();		
//		
//		projectRepository.deleteAll();		
//
//		simulationModelRepository.deleteAll();
//
//		
//		File tmpModel = File.createTempFile("simModel", ".txt");
//		FileUtils.writeStringToFile(tmpModel, "Hello File");
//
//		byte[] tmpModelarr = Helper.getFileBytes(tmpModel);
//
//		SimulationModel model = new SimulationModel();
//		model.setModelblob(tmpModelarr);
//		model.setSimulator("APROS");
//		model.setDescription("My first model");
//
//		simulationModelRepository.saveAndFlush(model);
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test	
	@Rollback(true)
	public void CreateSimulationModel() throws IOException {

		Integer sizeBefore = simulationModelRepository.findAll().size();
		
		File tmpModel = File.createTempFile("simModel", ".txt");
		FileUtils.writeStringToFile(tmpModel, "Hello File");

		byte[] tmpModelarr = Helper.getFileBytes(tmpModel);

		SimulationModel model = new SimulationModel();
		model.setModelblob(tmpModelarr);
		model.setSimulator("APROS");
		model.setDescription("My second model");

		simulationModelRepository.saveAndFlush(model);
		
		assertEquals((sizeBefore+1),simulationModelRepository.findAll().size());
	}

	@Test
	@Rollback(true)
	public void UpdateSimulationModel() throws IOException {

		String modelDesc= "My first model";		
						
		SimulationModel model = simulationModelRepository.findByDescription(modelDesc).get(0);
		model.setDescription("Model third updated");
		
		assertNotEquals(modelDesc, model.getDescription());
		
	}

	@Test
	@Rollback(true)
	public void AssignModelToProject_1() throws IOException {

		String modelDesc= "My first model";
				
		Project project = new Project();
		project.setName("Project 10");
		project.setLocation("Wien");
		
		projectRepository.saveAndFlush(project);
		
		assertNotEquals(0,project.getPrjid());

		SimulationModel model = simulationModelRepository.findByDescription(modelDesc).get(0);
		project.setSimulationmodel(model);
		
		assertNotEquals(null,project.getSimulationmodel());
		
	}	
	
	@Test
	@Rollback(true)
	public void AssignModelToProject_2() throws IOException {
		
		File tmpModel = File.createTempFile("simModel", ".txt");
		FileUtils.writeStringToFile(tmpModel, "Hello File");

		byte[] tmpModelarr = Helper.getFileBytes(tmpModel);

		SimulationModel model = new SimulationModel();
		model.setModelblob(tmpModelarr);
		model.setSimulator("APROS");
		model.setDescription("My third model");
		
		Project project = new Project();
		project.setName("Project 10");
		project.setLocation("Wien");
		project.setSimulationmodel(model);
		
		projectRepository.saveAndFlush(project);
		
		assertNotEquals(0,project.getPrjid());
		assertNotEquals(null,project.getSimulationmodel());
		
	}	

	@Test	
	@Rollback(true)
	public void GetSimulationModel_FileTest() throws IOException {
		String modelDesc= "My first model";

		SimulationModel model = simulationModelRepository.findByDescription(modelDesc).get(0);
		
		byte[] tmpModelarr = model.getModelblob();
		
		String content = new String(tmpModelarr, StandardCharsets.UTF_8);
		
		assertEquals("this is a test file for dbunit blob data.",content);
	}
	
	@Test	
	@Rollback(true)
	public void GetSimulationModel_FileTest_2() throws IOException {
		String modelDesc= "model with Base64";

		SimulationModel model = simulationModelRepository.findByDescription(modelDesc).get(0);
		
		byte[] tmpModelarr = model.getModelblob();
		
		String content = new String(tmpModelarr, StandardCharsets.UTF_8);
		
		assertEquals("this is a test file for dbunit blob data.",content);
	}
	
	@Test
	@Rollback(true)
	public void DeleteSimulationModel() throws IOException {
		
		String modelDesc= "My first model";

		SimulationModel model = simulationModelRepository.findByDescription(modelDesc).get(0);
		int modelID = model.getModelid();

		assertNotEquals(0, modelID);

		simulationModelRepository.delete(modelID);

		assertEquals(null, simulationModelRepository.findOne(modelID));
	}

}
