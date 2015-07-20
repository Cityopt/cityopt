package eu.cityopt.service.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler implements MethodSecurityExpressionHandler  {

    // parent constructor
    public CustomMethodSecurityExpressionHandler() {
        super();
    }
    
    
    @Override
    public StandardEvaluationContext createEvaluationContextInternal(
    		Authentication auth, MethodInvocation mi) {
    	   StandardEvaluationContext ctx = (StandardEvaluationContext) super.createEvaluationContext(auth, mi);
           ctx.setRootObject( new CustomSecurityExpressionRoot(auth) );
           return ctx;
    }
    
    
    
}