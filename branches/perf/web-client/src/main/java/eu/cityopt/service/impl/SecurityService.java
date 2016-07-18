package eu.cityopt.service.impl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {
    public boolean hasPermission(String key) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	
        return true;
    }
}