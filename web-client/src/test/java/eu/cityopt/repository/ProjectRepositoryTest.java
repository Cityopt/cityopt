package eu.cityopt.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
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

import eu.cityopt.model.Component;
import eu.cityopt.model.Project;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.UserGroupProjectRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class ProjectRepositoryTest {

	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired 
	UserGroupProjectRepository userGroupProjectRepository;
	
	@Autowired 
	ComponentRepository componentRepository;
	
	@PersistenceContext
	EntityManager em;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {	
		
	}

	@After
	public void tearDown() throws Exception {	
	
		
	}
	
	
	@Test	
	@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
	public void CreateProject()
	{	
		int sizeBefore = projectRepository.findAll().size();
		Project project = new Project();

		project.setName("Project 2");
		project.setLocation("Vienna");
		
		project = projectRepository.saveAndFlush(project);
		
		assertEquals(sizeBefore +1, projectRepository.findAll().size());
		assertNotEquals(0,project.getPrjid());	
	}
	
	@Test
	@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
	public void UpdateProject()
	{	
		String location;
		List<Project> projects = projectRepository.findByNameContainingIgnoreCase("Project 1");
		
		Project fproject = projects.get(0);
		location = fproject.getLocation();
		fproject.setLocation("Graz");
		
		assertNotEquals(location,fproject.getLocation());	
	}	
	
	@Test
	@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
	public void getProjectDescription()
	{	
		String location;
		List<Project> projects = projectRepository.findByNameContainingIgnoreCase("Project 1");
		Project fproject = projects.get(0);
		assertEquals("this is a good description",fproject.getDescription());	
	}	
		
	@Test
	@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
	public void findAll() {
	
		List<Project> projects = projectRepository.findAll();
		assertEquals(2, projects.size());	
	}
	
	@Test
	@DatabaseSetup("classpath:/testData/scenario_TestData.xml")
	public void findByName() {
	
		List<Project> projects = projectRepository.findByNameContainingIgnoreCase("Project 1");
		assertEquals(1, projects.size());	
		
		projects = projectRepository.findByNameContainingIgnoreCase("Project");
		assertEquals(2, projects.size());
		
		projects = projectRepository.findByNameContainingIgnoreCase("Projedsct");
		assertEquals(0, projects.size());
	}
	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml"})
	public void DeleteProject()
	{		
		List<Project> projects = projectRepository.findByNameContainingIgnoreCase("project");
		
		//delete projects components to be able to delete project if no cascadetype is set
//		for(Iterator<Project> p = projects.iterator(); p.hasNext();){
//			Project item = p.next();
//			
//			List<Component> comp = item.getComponents();
//			
//			componentRepository.delete(comp);
//		}
		
		projectRepository.delete(projects);
		
		assertEquals(0, projectRepository.findByNameContainingIgnoreCase("project").size());	
	}	

	
	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void findByName_QueryByMethodName_Test()
	{		
		List<Project> projects = projectRepository.findByNameLikeIgnoreCase("Project",new Sort(Direction.ASC,"name"));
		
		assertEquals(0, projects.size());
		
		projects = projectRepository.findByNameLikeIgnoreCase("sample Project",new Sort(Direction.ASC,"name"));
		
		assertEquals(1, projects.size());
	}
}
