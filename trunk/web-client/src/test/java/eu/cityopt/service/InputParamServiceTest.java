package eu.cityopt.service;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Date;
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

import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.TimeSeriesValDTO;
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
	ScenarioService scenarioService;

	@Autowired
	TimeSeriesValService timeSeriesValService;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void findById() throws EntityNotFoundException {
		InputParameterDTO iparam = inputParamService.findByID(1);
		UnitDTO u = iparam.getUnit();
		
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
	public void findByNameAndComponent() throws EntityNotFoundException {
		InputParameterDTO iparam = inputParamService.findByNameAndComponent("InputParameter 1",1);
		assertNotNull(iparam);
		
		iparam = inputParamService.findByNameAndComponent("InputParameter 1",4);
		assertNull(iparam);
	}
	
	@Test
	public void findInputParameterValues() throws EntityNotFoundException {
		
		Page<InputParamValDTO> inputParamVal = inputParamValService.findByComponentAndScenario(1, 1,0);
		
		assertNotNull(inputParamVal);		
		assertEquals(2,inputParamVal.getNumberOfElements());
		assertEquals(1,inputParamVal.getTotalPages());
		
	}

	@Test
	public void updateTimeSeries() throws EntityNotFoundException {
		InputParameterDTO inputParam = inputParamService.findByID(1);
		ScenarioDTO scenario = scenarioService.findByID(1);

		TimeSeriesDTOX tsd = new TimeSeriesDTOX();
		tsd.setTimes(new Date[] {
				Date.from(Instant.parse("2016-01-01T00:00:00Z")),
				Date.from(Instant.parse("2016-01-02T01:00:00Z")),
				Date.from(Instant.parse("2016-01-03T02:00:00Z")) });
		tsd.setValues(new double[] { 4, 5, 6 });

		InputParamValDTO iv = inputParamValService.findByInputAndScenario(
				inputParam.getInputid(), scenario.getScenid());
		inputParamValService.update(iv, tsd);

		InputParamValDTO nv = inputParamValService.findByInputAndScenario(
				inputParam.getInputid(), scenario.getScenid());
		assertNotNull(nv.getTimeseries());

		List<TimeSeriesValDTO> tsv = timeSeriesValService.findByTimeSeriesIdOrderedByTime(nv.getTimeseries().getTseriesid());
		assertEquals(3, tsv.size());
		for (int i = 0; i < tsv.size(); ++i) {
			assertEquals(tsd.getTimes()[i], tsv.get(i).getTime());
			assertEquals(tsd.getValues()[i], Double.parseDouble(tsv.get(i).getValue()), 1e-12);
		}
	}
}
