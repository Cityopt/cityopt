package eu.cityopt.repository;

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

import eu.cityopt.model.ExtParamValSet;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", 
	"classpath:/testData/project1TestData.xml"})
public class ExtParamValSetRepositoryTest {

	@Autowired
	private ExtParamValSetRepository extParamValSetRepository;
	
	@Test
	public void testFindByNameAndPrjid() {
		ExtParamValSet epvs = extParamValSetRepository.findByNameAndProject(1, "epvs1");
		assertNotNull(epvs);
		
		epvs = extParamValSetRepository.findByNameAndProject(1, "epvs1 asd");
		assertNull(epvs);
		
		epvs = extParamValSetRepository.findByNameAndProject(2, "epvs1");
		assertNull(epvs);
	}

}
