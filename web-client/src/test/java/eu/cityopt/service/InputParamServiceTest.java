package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.TimeSeriesDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
public class InputParamServiceTest {
	@Autowired
	InputParameterService inputParamService;
	
	@Autowired
	InputParamValService inputParamValService;
	
	@Autowired
	OutputVariableService outputVariableService;
	
	@Autowired
	TypeService typeService;
	
	@Autowired
	UnitService unitService;
	
	//Type		
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void findById() throws EntityNotFoundException {
		InputParameterDTO iparam = inputParamService.findByID(1);
		UnitDTO u = iparam.getUnit();
		TimeSeriesDTO timeseries = iparam.getTimeseries();
		
//		int uid = iparam.getUnitID();
//		int cid = iparam.getComponentID();
		
		assertEquals("myUnit", u.getName());
		assertEquals("InputParameter 1", iparam.getName());
		assertEquals(1, inputParamService.getComponentId(iparam.getInputid()));
//		assertTrue(uid == 1);
//		assertTrue(cid == 1);
		
	}
	
	@Test
	public void findValuesById() throws EntityNotFoundException {
		Set<InputParamValDTO> iparamVal = inputParamService.getInputParamVals(1);
		assertNotNull(iparamVal);
		assertTrue(iparamVal.size() > 0);

	}
	
	@Test
	public void findValuesByName() throws EntityNotFoundException {
		List<InputParameterDTO> iparam = inputParamService.findByName("parameter");
		assertNotNull(iparam);
		assertTrue(iparam.size() == 2);
	}
	
	@Test
	public void updateType() throws EntityNotFoundException {
		OutputVariableDTO outputVariable = outputVariableService.findByID(1);				
		
		TypeDTO type = typeService.findByID(1);
		UnitDTO unit = unitService.findByID(1);
		
		//outputVariable.setType(type);
		outputVariable.setUnit(unit);
		outputVariableService.update(outputVariable);
		
	}
	
	@Test
	public void findInputParameterValues() throws EntityNotFoundException {
		
		Page<InputParamValDTO> inputParamVal = inputParamValService.findByComponentAndScenario(1, 1,0);
		
		assertNotNull(inputParamVal);		
		assertEquals(2,inputParamVal.getNumberOfElements());
		assertEquals(1,inputParamVal.getTotalPages());
		
	}

}
