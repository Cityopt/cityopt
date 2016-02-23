package eu.cityopt.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParameterService;

@Component
public class InputParameterValidator implements Validator {
	@Autowired
	InputParameterService inputParamService;
	
	@Autowired
	InputParamValService inputParamValService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return InputParamValDTO.class.isAssignableFrom(clazz) || InputParameterDTO.class.isAssignableFrom(clazz);		
	}

	@Override
	public void validate(Object target, Errors errors) {	
		
		if(target instanceof InputParameterDTO)
		{
			InputParameterDTO inputparameter = (InputParameterDTO) target;
			
			if(!StringUtils.isEmpty(inputparameter.getRegexValid()))
			{
				if(!Pattern.matches(inputparameter.getRegexValid(), inputparameter.getDefaultvalue()))
				{
					errors.reject(String.format("%s does not meet %s",inputparameter.getDefaultvalue(),inputparameter.getRegexValid()));					
				}
			}
		}
		else if(target instanceof InputParamValDTO)
		{			
			InputParamValDTO iVal = (InputParamValDTO)target;
			InputParameterDTO inputparameter = iVal.getInputparameter();
			
			if(!StringUtils.isEmpty(inputparameter.getRegexValid()))
			{
				if(!Pattern.matches(inputparameter.getRegexValid(), iVal.getValue()))
				{
					errors.reject(String.format("%s does not meet %s",iVal.getValue(),inputparameter.getRegexValid()));					
				}
			}
			
			
			
		}
		
		
	}

}
