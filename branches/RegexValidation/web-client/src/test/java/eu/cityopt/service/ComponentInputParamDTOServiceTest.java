package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;

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

import eu.cityopt.DTO.ComponentInputParamDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC1.xml", "classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC2.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC3.xml", "classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC4.xml",
	"classpath:/testData/SampleTestCaseNoResults/Sample Test case - SC5.xml"})
public class ComponentInputParamDTOServiceTest {
	@Autowired
	ComponentInputParamDTOService cipDTOService;	
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
//	@Rollback(false)
	public void testFindByPrjAndScen() throws EntityNotFoundException {
		List<ComponentInputParamDTO> list = cipDTOService.findAllByPrjAndScenId(1, 1);

	 	for(ComponentInputParamDTO item : list) {
	 		System.out.print(item.getComponentid() + " | ");
	 		System.out.print(item.getComponentname() + " | ");
	 		System.out.print(item.getInputid() + " | ");
	 		System.out.print(item.getInputparametername() + " | ");
	 		System.out.print(item.getInputparamvalid() + " | ");
	 		System.out.print(item.getValue() + " | ");
	 		System.out.print(item.getScenarioid() + " | ");
	 		System.out.print(item.getPrjid() + " | ");
	 		System.out.println();
	 	}
	 	
	 	assertNotNull(list);	
	 	assertEquals(13, list.size());
	}

}

