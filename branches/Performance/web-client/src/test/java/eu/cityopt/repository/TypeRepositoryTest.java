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

import eu.cityopt.model.Type;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml"})
public class TypeRepositoryTest {

	@Autowired
	private TypeRepository typeRepository;
	
	@Test
	public void findByNameLike() {
		Type type = typeRepository.findByNameLike("Double");

		assertNotNull(type);
		
		type = typeRepository.findByNameLike("double");

		assertNull(type);		
	}

	@Test
	public void findByNameLikeIgnoreCase() {
		List<Type> types = typeRepository.findByNameLikeIgnoreCase("Double");

		assertEquals(1, types.size());
		
		types = typeRepository.findByNameLikeIgnoreCase("double");

		assertEquals(1, types.size());		
	}

	@Test
	public void findByNameContaining() {
		List<Type> types = typeRepository.findByNameContainingIgnoreCase("in");

		assertEquals(4, types.size());
		
		types = typeRepository.findByNameContainingIgnoreCase("list of double");

		assertEquals(1, types.size());		
	}
}
