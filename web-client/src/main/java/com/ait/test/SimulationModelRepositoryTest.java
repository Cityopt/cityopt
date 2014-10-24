package com.ait.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ait.helper.Helper;
import com.ait.model.Project;
import com.ait.model.Simulationmodel;
import com.ait.repository.ProjectRepository;
import com.ait.repository.SimulationModelRepository;
import com.ait.repository.UserGroupProjectRepository;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/test-context.xml" })
@Transactional
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
		
		userGroupProjectRepository.deleteAll();		
		
		projectRepository.deleteAll();		

		simulationModelRepository.deleteAll();

		
		File tmpModel = File.createTempFile("simModel", ".txt");
		FileUtils.writeStringToFile(tmpModel, "Hello File");

		byte[] tmpModelarr = Helper.getFileBytes(tmpModel);

		Simulationmodel model = new Simulationmodel();
		model.setModelblob(tmpModelarr);
		model.setModelsimulator("APROS");
		model.setModeldesc("My first model");

		simulationModelRepository.saveAndFlush(model);
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test	
	@Rollback(true)
	public void CreateSimulationModel() throws IOException {

		File tmpModel = File.createTempFile("simModel", ".txt");
		FileUtils.writeStringToFile(tmpModel, "Hello File");

		byte[] tmpModelarr = Helper.getFileBytes(tmpModel);

		Simulationmodel model = new Simulationmodel();
		model.setModelblob(tmpModelarr);
		model.setModelsimulator("APROS");
		model.setModeldesc("My second model");

		simulationModelRepository.saveAndFlush(model);
		
		assertEquals(2,simulationModelRepository.findAll().size());
	}

	@Test
	@Rollback(true)
	public void UpdateSimulationModel() throws IOException {

		String modelDesc= "My first model";		
						
		Simulationmodel model = simulationModelRepository.findByDescription(modelDesc).get(0);
		model.setModeldesc("Model third updated");
		
		assertNotEquals(modelDesc, model.getModeldesc());
		
	}

		@Test
	@Rollback(true)
	public void AssignModelToProject_1() throws IOException {

		String modelDesc= "My first model";
				
		Project project = new Project();
		project.setPrjname("Project 10");
		project.setPrjlocation("Wien");
		
		projectRepository.saveAndFlush(project);
		
		assertNotEquals(0,project.getPrjid());

		Simulationmodel model = simulationModelRepository.findByDescription(modelDesc).get(0);
		project.setSimulationmodel(model);
		
		assertNotEquals(null,project.getSimulationmodel());
		
	}	
	
	@Test
	@Rollback(true)
	public void DeleteSimulationModel() throws IOException {
		
		String modelDesc= "My first model";

		Simulationmodel model = simulationModelRepository.findByDescription(modelDesc).get(0);
		int modelID = model.getModelid();

		assertNotEquals(0, modelID);

		simulationModelRepository.delete(modelID);

		assertEquals(null, simulationModelRepository.findOne(modelID));
	}

}
