package eu.cityopt.validators;

import static org.junit.Assert.*;

import java.util.Set;

import org.aspectj.lang.annotation.Before;
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
import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParameterService;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:/testData/inputParameter_TestData.xml")
public class InputParamValidatorTest {
	private Validator validator;

	@Autowired
	InputParameterService inputParamService;
	
	@Autowired
	InputParamValService inputParamValService;
	
	@org.junit.Before
    public void setUp() {
        validator = new InputParameterValidator();
    }

    @Test public void supports() {
        assertTrue(validator.supports(InputParamVal.class));
        assertFalse(validator.supports(Object.class));
    }

    @Transactional
    @Test public void inputParamIsValid() {
    	 Set<InputParamValDTO> iparamVal = inputParamService.getInputParamVals(2);
    	 
    	 InputParamValDTO iParamVal = iparamVal.iterator().next();
		
    	 BindException errors = new BindException(iParamVal, "inputParamValDTO");
         ValidationUtils.invokeValidator(validator, iParamVal, errors);
         assertFalse(errors.hasErrors());
    }
}
