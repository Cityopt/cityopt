package eu.cityopt.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Project;
import eu.cityopt.model.SimulationModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test-context.xml"})
@Transactional
public class ProjectServiceTest {

	@Autowired
	ProjectService projectService;
	
	@Autowired
	SimulationModelService simulationModelService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		projectService.deleteAll();
		simulationModelService.deleteAll();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test	
	public void CreateProject() throws IOException {
		
		Project project_2 = new Project();
		project_2.setName("Project 2");
		project_2.setLocation("Graz");		
		
		File tmpModel = File.createTempFile("simModel",".txt");
		FileUtils.writeStringToFile(tmpModel, "Hello File");		 
		
		byte[] tmpModelarr = getFileBytes(tmpModel);
		
		SimulationModel model = new SimulationModel();
		model.setModelblob(tmpModelarr);
		model.setSimulator("APROS");
		model.setDescription("My seconds model");				
		
		project_2.setSimulationmodel(model);
		
		projectService.save(project_2);
		
		Project fproject = projectService.findByID(project_2.getPrjid());
		SimulationModel modact = fproject.getSimulationmodel();
		String mydesc = modact.getDescription();
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

}