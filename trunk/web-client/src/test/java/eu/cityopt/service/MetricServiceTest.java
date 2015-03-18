package eu.cityopt.service;

import static org.junit.Assert.*;

import java.util.List;
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

import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/MetricValues_TestData.xml"})
public class MetricServiceTest {

	@Autowired
	private MetricService metricService;
	
	@Test
	public void getMetricValues() throws EntityNotFoundException {
		MetricDTO m1 = metricService.findByID(1);
		
		Set<MetricValDTO> mvs = metricService.getMetricVals(m1.getMetid());
		
		assertEquals(1, mvs.size());
		
		mvs = metricService.getMetricVals(3);
		assertEquals(2, mvs.size());
		
		mvs = metricService.getMetricVals(4);
		assertEquals(0, mvs.size());
	}

}
