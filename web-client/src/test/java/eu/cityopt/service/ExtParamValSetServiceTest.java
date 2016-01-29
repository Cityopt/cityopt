package eu.cityopt.service;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
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
	
	@Autowired
	private ProjectService projectService;
	
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
		
		List<ExtParamDTO> epList = extParamService.findByName("Cost_of_the_N_Gas");
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
	
	@Test
	public void removeExtParamVals() throws EntityNotFoundException {
		
		List<ExtParamDTO> epList = extParamService.findByName("Cost_of_the_N_Gas");
		epList.addAll(extParamService.findByName("Emissions_N_Gas"));
		
		List<ExtParamValDTO> epv = extParamValSetService.getExtParamVals(1);
		ExtParamValDTO val = epv.get(2);
		epv.remove(2);
		extParamValSetService.removeExtParamValsFromSet(1, new HashSet<ExtParamValDTO>(epv));
		
		epv = extParamValSetService.getExtParamVals(1);
		
		assertEquals(1, epv.size());
		assertEquals(epv.iterator().next().getExtparam().getName(), val.getExtparam().getName());
	}

	@Test
	public void updateValueInSetOrClone() throws EntityNotFoundException {
        List<ExtParamDTO> epList = extParamService.findByName("Cost_of_the_N_Gas");
        ExtParamValDTO newEPV = new ExtParamValDTO();
        newEPV.setExtparam(epList.iterator().next());
        newEPV.setValue("20.0");

        extParamValSetService.updateExtParamValInSetOrClone(1, newEPV, null);

        List<ExtParamValDTO> epv = extParamValSetService.getExtParamVals(1);   
        assertEquals(3, epv.size());
        
        String [] epArr = new String [] { "Specific_Heat_Water", "Cost_of_the_N_Gas", "Emissions_N_Gas"};
        List<String> epListRes = Arrays.asList(epArr);
        
        for(ExtParamValDTO epvDTO : epv){
            assertTrue( epListRes.contains(epvDTO.getExtparam().getName()) );
            if (epvDTO.getExtparam().getName().equals("Cost_of_the_N_Gas")) {
                assertEquals("20.0", epvDTO.getValue());
            }
        }
	}

    @Test
    public void updateTimeSeriesInSetOrClone() throws EntityNotFoundException {
        List<ExtParamDTO> epList = extParamService.findByName("Cost_of_the_N_Gas");
        ExtParamValDTO newEPV = new ExtParamValDTO();
        newEPV.setExtparam(epList.iterator().next());
        TimeSeriesDTOX tsDTO = new TimeSeriesDTOX();
        tsDTO.setTimes(new Date[] {
                Date.from(Instant.parse("2015-01-01T00:00:00Z")),
                Date.from(Instant.parse("2016-01-01T00:00:00Z"))
                });
        tsDTO.setValues(new double[] { 10.0, 100.0 });

        extParamValSetService.updateExtParamValInSetOrClone(1, newEPV, tsDTO);

        List<ExtParamValDTO> epv = extParamValSetService.getExtParamVals(1);   
        assertEquals(3, epv.size());
        
        String [] epArr = new String [] { "Specific_Heat_Water", "Cost_of_the_N_Gas", "Emissions_N_Gas"};
        List<String> epListRes = Arrays.asList(epArr);
        
        for(ExtParamValDTO epvDTO : epv){
            assertTrue( epListRes.contains(epvDTO.getExtparam().getName()) );
            if (epvDTO.getExtparam().getName().equals("Cost_of_the_N_Gas")) {
                assertEquals(null, epvDTO.getValue());
            }
        }
    }
    
    @Test
    public void addExtParam()
    {
    	ExtParamDTO extParam = new ExtParamDTO();
    	extParam.setName("MyNewTestParam");
    	
    	Integer extParamValSetID = projectService.getDefaultExtParamSetId(1);
    	
    	assertNull(extParamValSetID);
    	
    	extParamService.save(extParam, 1);    	
    	
    	extParamValSetID = projectService.getDefaultExtParamSetId(1);
    	
    	assertNotNull(extParamValSetID);   	
    	
    }
    
    
}
