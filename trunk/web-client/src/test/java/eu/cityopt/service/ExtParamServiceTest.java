package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.Set;

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

import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.repository.ExtParamRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
public class ExtParamServiceTest {
	@Autowired
	private ExtParamService extParamService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private UnitService unitService;
	
	@Test
	public void extParam_UnitReferenceTest() throws EntityNotFoundException {
		ProjectDTO prj = projectService.findByID(1);
		Set<ExtParamDTO> extParams = projectService.getExtParams(1);
		
		ExtParamDTO newParam = new ExtParamDTO();
		newParam.setName("my new Param");
		UnitDTO unit = unitService.findByID(5);
		newParam.setUnit(unit);
		newParam = extParamService.save(newParam, 1);
		
		assertNotNull(newParam);
		assertNotNull(newParam.getUnit().getName());
		assertEquals(newParam.getUnit().getName(), unit.getName());
	}

}
