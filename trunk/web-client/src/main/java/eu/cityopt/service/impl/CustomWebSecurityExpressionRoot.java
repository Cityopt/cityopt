package eu.cityopt.service.impl;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

public class CustomWebSecurityExpressionRoot extends WebSecurityExpressionRoot  {

    public CustomWebSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
		super(a, fi);
		// TODO Auto-generated constructor stub
	}

	/**
     * Pass through to hasRole preserving Entitlement method naming convention
     * @param expression
     * @return boolean
     */
    public boolean hasEntitlement(String expression) {
        return hasRole(expression);
    }
    
    public boolean hasTestRole(String expression) {
        return true;
    }    
    

}