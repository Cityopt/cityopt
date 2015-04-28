package eu.cityopt.repository;

import static org.junit.Assert.*;

import java.util.List;

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



//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
"classpath:/testData/Sample Test case - SC1.xml"})
public class SimulationResultRepositoryTest {

	@Autowired
	SimulationResultRepository simulationResultRepository;
	
	@Autowired
	UnitRepository unitRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Test	
	@Rollback(true)
	public void findByScenID() {
		List<SimulationResult> simResults = simulationResultRepository.findByScenId(1);
		
		assertEquals(4, simResults.size());
		
		for(SimulationResult simres : simResults){
			TimeSeries ts = simres.getTimeseries();
			assertNotNull(ts);
			assertTrue(ts.getTimeseriesvals().size() > 100);
		}
		
		simResults = simulationResultRepository.findByScenId(33);
		assertEquals(0,simResults.size());
	}
	
}
