package eu.cityopt.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

//@author Markus Turunen.

/* These are Spring security annotations, 
 * They are invoked before the method call the and intercept the method call if they fail
 * authentication. They use ProjectPreAuthorization Class' methods to authenticate user's authorization.
 * ProjectPreAuthorization class can be found at  web-client/scr/main/java/eu.cityopt.validators
 * Once the the secured resource's method is called and passed authentication the process of the invoking the
 * method can go on as normal. Otherwise they are directed into 404 Access denied page.
 */

@Component
public class SecurityAuthorization {
	
	@PreAuthorize("hasRole('ROLE_Administrator')")
	public void atLeastAdmin(Object project){		
	
	}
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasRole('ROLE_Expert') and ("
			+" hasPermission(#project,'ROLE_Administrator')"
										+ "))")
	public void atLeastExpert_admin(Object project){
		    	
		    }
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasRole('ROLE_Expert') and ("
		    	+" hasPermission(#project,'ROLE_Administrator') or"
		    	+" hasPermission(#project,'ROLE_Expert')"
		    						+ "))")
	public void atLeastExpert_expert(Object project){
		
	}
		
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasRole('ROLE_Expert') and ("
		    	+" hasPermission(#project,'ROLE_Administrator') or"
		    	+" hasPermission(#project,'ROLE_Expert') or"
		    	+" hasPermission(#project,'ROLE_Standard')"
		    						+ "))")
	public void atLeastExpert_standard(Object project){
		
	}
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasRole('ROLE_Expert') and ("
		    	+" hasPermission(#project,'ROLE_Administrator') or"
		    	+" hasPermission(#project,'ROLE_Expert') or"
		    	+" hasPermission(#project,'ROLE_Standard') or"
		    	+" hasPermission(#project,'ROLE_Guest')"
		    						+ "))")
	public void atLeastExpert_guest(Object project){
		
	}	
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasRole('ROLE_Expert') or hasRole('ROLE_Standard') and ("
			+" hasPermission(#project,'ROLE_Administrator')"
										+ "))")
	public void atLeastStandard_admin(Object project){
		    	
		    }
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasRole('ROLE_Expert') or hasRole('ROLE_Standard') and ("
			+" hasPermission(#project,'ROLE_Administrator') or"
			+" hasPermission(#project,'ROLE_Expert')"
										+ "))")
	public void atLeastStandard_expert(Object project){
		    	
		    }
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasRole('ROLE_Expert') or hasRole('ROLE_Standard') and ("
			+" hasPermission(#project,'ROLE_Administrator') or"
			+" hasPermission(#project,'ROLE_Expert') or"
			+" hasPermission(#project,'ROLE_Standard')"
										+ "))")
	public void atLeastStandard_standard(Object project){
		    	
		    }
		
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
				+" hasRole('ROLE_Expert') or hasRole('ROLE_Standard') and ("
		    	+" hasPermission(#project,'ROLE_Administrator') or"
		    	+" hasPermission(#project,'ROLE_Expert') or"
		    	+" hasPermission(#project,'ROLE_Standard') or"
		    	+" hasPermission(#project,'ROLE_Guest')"
		    						+ "))")
	public void atLeastStandard_guest(Object project){		
	}
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" isAuthenticated() and ("
		    	+" hasPermission(#project,'ROLE_Administrator') or"
		    	+" hasPermission(#project,'ROLE_Expert') or"
		    	+" hasPermission(#project,'ROLE_Standard') or"
		    	+" hasPermission(#project,'ROLE_Guest')"
		    						+ "))")
	public void atLeastGuest_guest(Object project){		
	}
				
}
	