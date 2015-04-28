package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;

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

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.AlgoParamValDTO;
import eu.cityopt.DTO.AlgorithmDTO;
import eu.cityopt.DTO.ScenGenObjectiveFunctionDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/plumbing_scengen.xml"})
public class ScenarioGeneratorServiceTest {

	@Autowired
	ScenarioGeneratorService scenarioGeneratorService;
	
	@Autowired
	AlgorithmService algorithmService;
	
//	@Test
//	public void testScenarioGeneratorService() {
//		ScenarioGeneratorDTO sgDTO = scenarioGeneratorService.findAll().iterator().next();
//		
//		assertEquals(8, sgDTO.getAlgoparamvals().size());
//		for(AlgoParamValDTO apvd : sgDTO.getAlgoparamvals()){
//			System.out.println("AlgoParam: \n name:" 
//					+ apvd.getAlgoparam().getName() + "\n val:"
//					+ apvd.getValue() + "\n descr: "
//					+ apvd.getAlgoparam().getAlgorithm().getDescription());
//		}
//	}

	@Test
	public void testScenarioGeneratorService_getAlgorithm() throws EntityNotFoundException {
		ScenarioGeneratorDTO sgDTO = scenarioGeneratorService.findAll().iterator().next();
		
		List<AlgoParamDTO> apDTO = algorithmService.getAlgoParams(2);
		
		assertEquals(6, apDTO.size());
		for(AlgoParamDTO apvd : apDTO){
			System.out.println("AlgoParam: \n name:" 
					+ apvd.getName() + "\n descr:"
					+ apvd.getAlgorithm().getDescription());
		}
	}
	
	@Test
	public void testScenarioGeneratorService_findWithoutValues() throws EntityNotFoundException {
		ScenarioGeneratorDTO sgDTO = scenarioGeneratorService.findAll().iterator().next();
		
		List<AlgoParamDTO> apDTO = scenarioGeneratorService.getAlgoParams(1);

		assertEquals(2, apDTO.size());
		for(AlgoParamDTO apvd : apDTO){
			System.out.println("AlgoParam: \n name:" 
					+ apvd.getName() + "\n descr:"
					+ apvd.getAlgorithm().getDescription());
		}
	}
	
}
