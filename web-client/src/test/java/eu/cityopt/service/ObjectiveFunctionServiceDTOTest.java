package eu.cityopt.service;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.ObjectiveFunctionResultDTO;
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.SimulationModelDTO;
import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ScenarioRepository;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class ObjectiveFunctionServiceDTOTest {

	@Autowired ObjectiveFunctionService objectiveFunctionService;

	@PersistenceContext
	EntityManager em;

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected=EntityNotFoundException.class)
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void findByName() throws EntityNotFoundException {

		ObjectiveFunctionDTO objectiveFunction = objectiveFunctionService.findByName(1, "ObjectiveFunction 1");
		assertNotNull(objectiveFunction);

            	objectiveFunctionService.findByName(1, "ObjectiveFunction 2");
	}

	@Test
	@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml"})
	public void existsByName() throws EntityNotFoundException {

		assertTrue(objectiveFunctionService.existsByName(1, "ObjectiveFunction 1"));

		assertFalse(objectiveFunctionService.existsByName(1, "ObjectiveFunction 2"));
	}

	@Test
	@DatabaseSetup({"classpath:/testData/plumbing_ga_result2.xml"})
	public void findObjectiveFunctionResults()
	{
		List<ObjectiveFunctionResultDTO> results = objectiveFunctionService.findResultsByScenarioGenerator(1, 1);
		ObjectiveFunctionResultDTO objectiveFunctionResultDTO = results.get(0);

		assertEquals(true,objectiveFunctionResultDTO.isScengenresultFeasible());
		assertEquals(true,objectiveFunctionResultDTO.isScengenresultParetooptimal());

		assertEquals(1,results.size());
		assertEquals(103,objectiveFunctionResultDTO.getScengenid());

	}

}
