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

@Component("inputParameterValidator")
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
			
			if(!StringUtils.isEmpty(inputparameter.getDefaultvalue()) && !StringUtils.isEmpty(inputparameter.getLowerBound()) && !StringUtils.isEmpty(inputparameter.getUpperBound()))
			{
				Double defaultValue= Double.valueOf(inputparameter.getDefaultvalue());
				Double lowerBound = Double.valueOf(inputparameter.getLowerBound());
				Double upperBound = Double.valueOf(inputparameter.getUpperBound());
				
				if(!(defaultValue>=lowerBound && defaultValue<=upperBound))
				{
					errors.reject(String.format("%s with value %s not within the limits %s-%s",inputparameter.getName(),inputparameter.getDefaultvalue(),inputparameter.getLowerBound(),inputparameter.getUpperBound()));					
				}
			}
		}
		else if(target instanceof InputParamValDTO)
		{			
			InputParamValDTO iVal = (InputParamValDTO)target;
			InputParameterDTO inputparameter = iVal.getInputparameter();
			
			if(!StringUtils.isEmpty(iVal.getValue()) && !StringUtils.isEmpty(inputparameter.getLowerBound()) && !StringUtils.isEmpty(inputparameter.getUpperBound()))
			{
				Double paramValue= Double.valueOf(iVal.getValue());
				Double lowerBound = Double.valueOf(inputparameter.getLowerBound());
				Double upperBound = Double.valueOf(inputparameter.getUpperBound());
				
				if(!(paramValue>=lowerBound && paramValue<=upperBound))
				{
					errors.reject(String.format("%s with value %s not within the limits %s-%s",inputparameter.getName(), iVal.getValue(),inputparameter.getLowerBound(),inputparameter.getUpperBound()));					
				}
			}
			
			
			
		}
		
		
	}

}