package eu.cityopt.service.impl;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

public class CustomSecurityExpressionRoot extends SecurityExpressionRoot  {

    // parent constructor
    public CustomSecurityExpressionRoot(Authentication a) {
        super(a);
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