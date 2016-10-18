package eu.cityopt.security;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

//@author Markus Turunen.
/*
* @author Markus Turunen  
* My code is under Apache Licence (ASL) * This code library implement Spring Security 4.0.1 / April 23, 2015 
* 
* The Apache License (ASL) is a free software license written by the Apache Software Foundation (ASF).
* The Apache License requires preservation of the copyright notice and disclaimer. 
* Like other free software licenses, the license allows the user of the software the 
* freedom to use the software for any purpose, to distribute it, to modify it, and to distribute modified 
* versions of the software, under the terms of the license, without concern for royalties.
* 

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
	public void atLeastAdmin(){		
	
	}
	@PreAuthorize("hasAnyRole('ROLE_Administrator','ROLE_Expert')")
	public void atLeastExpert(){		
	
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
		    +" hasAnyRole('ROLE_Expert','ROLE_Standard') and ("
			+" hasPermission(#project,'ROLE_Administrator')"
										+ "))")
	public void atLeastStandard_admin(Object project){
		    	
		    }
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasAnyRole('ROLE_Expert','ROLE_Standard') and ("
			+" hasPermission(#project,'ROLE_Administrator') or"
			+" hasPermission(#project,'ROLE_Expert')"
										+ "))")
	public void atLeastStandard_expert(Object project){
		    	
		    }
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasAnyRole('ROLE_Expert','ROLE_Standard') and ("
			+" hasPermission(#project,'ROLE_Administrator') or"
			+" hasPermission(#project,'ROLE_Expert') or"
			+" hasPermission(#project,'ROLE_Standard')"
										+ "))")
	public void atLeastStandard_standard(Object project){
		    	
		    }
		
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
				+" hasAnyRole('ROLE_Expert','ROLE_Standard') and ("
		    	+" hasPermission(#project,'ROLE_Administrator') or"
		    	+" hasPermission(#project,'ROLE_Expert') or"
		    	+" hasPermission(#project,'ROLE_Standard') or"
		    	+" hasPermission(#project,'ROLE_Guest')"
		    						+ "))")
	public void atLeastStandard_guest(Object project){		
	}
	
	@PreAuthorize("hasRole('ROLE_Administrator') or ("
		    	+" hasAnyRole('ROLE_Expert','ROLE_Standard','ROLE_Guest') and ("
		    	+" hasPermission(#project,'ROLE_Administrator') or"
		    	+" hasPermission(#project,'ROLE_Expert') or"
		    	+" hasPermission(#project,'ROLE_Standard') or"
		    	+" hasPermission(#project,'ROLE_Guest')"
		    						+ "))")
	public void atLeastGuest_guest(Object project){		
	}
				
}
	