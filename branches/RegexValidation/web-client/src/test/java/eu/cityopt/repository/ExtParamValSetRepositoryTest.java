package eu.cityopt.repository;

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

import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;

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
	
	@Test
	public void testFindByPrjid() {
		List<ExtParamValSet> epvs = extParamValSetRepository.findByProject(1);
		assertNotNull(epvs);
		assertTrue(epvs.size() == 1);
		assertTrue(epvs.iterator().next().getExtparamvalsetcomps().size() == 3);
		
		epvs = extParamValSetRepository.findByProject(4);
		assertTrue(epvs.size() == 0);
	}

}
