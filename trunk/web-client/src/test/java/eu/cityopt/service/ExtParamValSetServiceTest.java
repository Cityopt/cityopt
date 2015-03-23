package eu.cityopt.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

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
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.repository.ExtParamRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
public class ExtParamValSetServiceTest {
	@Autowired
	private ExtParamValSetService extParamValSetService; 
	
	@Autowired
	private ExtParamRepository extParamRepository;
	
	@Autowired
	private ExtParamService extParamService;
	
	@Test
	public void getExtParamVals() throws EntityNotFoundException {
		
		List<ExtParamValDTO> epv = extParamValSetService.getExtParamVals(1);		
		
		assertEquals(3, epv.size());
		
		String [] epArr = new String [] { "Specific_Heat_Water", "Cost_of_the_N_Gas", "Emissions_N_Gas"};
		List<String> epList = Arrays.asList(epArr);
		
		for(ExtParamValDTO epvDTO : epv){
			assertTrue( epList.contains(epvDTO.getExtparam().getName()) );
		}
	}
	
	@Test
	public void setExtParamVals() throws EntityNotFoundException {
		
		List<ExtParamDTO> epList = extParamService.findByName("Cost");
		ExtParamValDTO newEPV = new ExtParamValDTO();
		newEPV.setExtparam(epList.iterator().next());
		newEPV.setValue("20.0");
		
		Set<ExtParamValDTO> epvSet = new HashSet<ExtParamValDTO>();
		epvSet.add(newEPV);
		extParamValSetService.addExtParamVals(1, epvSet);
		
		List<ExtParamValDTO> epv = extParamValSetService.getExtParamVals(1);		
		
		assertEquals(4, epv.size());
		
		String [] epArr = new String [] { "Specific_Heat_Water", "Cost_of_the_N_Gas", "Emissions_N_Gas"};
		List<String> epListRes = Arrays.asList(epArr);
		
		for(ExtParamValDTO epvDTO : epv){
			assertTrue( epListRes.contains(epvDTO.getExtparam().getName()) );
		}
	}
	
}
