package eu.cityopt.validators;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.UserGroupDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.config.AppMetadata;
import eu.cityopt.model.UserGroup;
import eu.cityopt.model.UserGroupProject;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.UserGroupProjectRepository;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.ComponentInputParamDTOService;
import eu.cityopt.service.ComponentService;
import eu.cityopt.service.CopyService;
import eu.cityopt.service.DatabaseSearchOptimizationService;
import eu.cityopt.service.DecisionVariableService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamService;
import eu.cityopt.service.ExtParamValService;
import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.service.ImportService;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParameterService;
import eu.cityopt.service.MetricService;
import eu.cityopt.service.MetricValService;
import eu.cityopt.service.ObjectiveFunctionService;
import eu.cityopt.service.OptConstraintService;
import eu.cityopt.service.OptSearchConstService;
import eu.cityopt.service.OptimizationSetService;
import eu.cityopt.service.OutputVariableService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioGeneratorService;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.SimulationResultService;
import eu.cityopt.service.TimeSeriesService;
import eu.cityopt.service.TimeSeriesValService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.service.UserGroupProjectService;
import eu.cityopt.service.UserGroupService;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;


//@author Markus Turunen.
/* @author Markus Turunen  
 * My code is under Apache Licence (ASL) * This code library implement Spring Security 4.0.1 / April 23, 2015 
 * 
 * The Apache License (ASL) is a free software license written by the Apache Software Foundation (ASF).
 * The Apache License requires preservation of the copyright notice and disclaimer. 
 * Like other free software licenses, the license allows the user of the software the 
 * freedom to use the software for any purpose, to distribute it, to modify it, and to distribute modified 
 * versions of the software, under the terms of the license, without concern for royalties.
 *   
 */

/* This is Project Permission evaluator, It's purpose is to authenticate user.
 * When spring annotations invoke the method it calls has permission method.
 * If anything is wrong it cancels the permission rights.
 * If the Authentication is passed it pass the authorization and invoke the method call.
 * If the credentials fail the user dosen't have access to the secured content medthod is intercepted.
 * and user is thrown into  into 404 error Access denied page.
*/

public class ProjectPermissionEvaluator implements PermissionEvaluator {
	  
	
		@Autowired
		UserGroupProjectRepository userGroupProjectRepository;
	    
	    @Autowired
	    AppUserService userService;
	  
	    @Autowired
	    UserGroupProjectService userGroupProjectService;
	  
	    @Autowired
	    UserGroupService userGroupService;
	  
	    
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
			
		try {
			
			// Store a permission Role required to authenticate.
			String permissionRole = (String) permission;
			
			// Find a corresponding project data based target domain object.
			int projectIDn;			
			projectIDn=this.TargetDomainTypeConverter(targetDomainObject);
			
			// Use Authenticated user to find Matching user from database
			AppUserDTO appuserdto;
			appuserdto=this.FindAuthenticatedUser(authentication);
			
			// Find UserGroup Project based on found user account and project
			int userID = appuserdto.getUserid();					
			UserGroupProjectDTO usergroupprojectdto = userGroupProjectService.findByUserAndProject(userID, projectIDn);
			if(usergroupprojectdto==null){
				return false;
			}
			// Find Usergroup of the target usergroup project.
			UserGroupDTO usergroupdto = usergroupprojectdto.getUsergroup();
			String userGroupName=usergroupdto.getName();
			
			// This is permission evaluator Do not delete!
			if (permissionRole!=null && usergroupdto!=null && usergroupdto.getName()!=null && userGroupName.equals(permissionRole)) {
				return true;
			} else {
				return false;
			}
			//----  That's all folks.
		} catch (Exception e1) {
			System.out.println(e1);
			return false;
		}

	}
	
	// Implementation methods:	
	public int TargetDomainTypeConverter(Object targetDomainObject) throws Exception{

		// if the TargetDomainObject is instance of String.
		if ( targetDomainObject instanceof String){				
			String projectID = (String) targetDomainObject;	
			int projectIDn= Integer.parseInt(projectID);
			return projectIDn;
			
		// if the TargetDomainObject is instance of Integer.	
		}else if(targetDomainObject instanceof Integer){
			int projectIDn= (int) targetDomainObject;
			return projectIDn;
			
		// if the TargetDomainObject is instance of ModelMap (WARNING: Unsafe declaration.)	
		}else if(targetDomainObject instanceof Map<?,?>){			
			@SuppressWarnings("unchecked")			
			Map<String, Object> model = (Map<String, Object>) targetDomainObject;
			System.out.println("Security Warning: Unsafe Type used as Permission evaluation");
			ProjectDTO project = (ProjectDTO) model.get("project");
			return project.getPrjid();			
		}
		
		// If the Target  domain object is instance of ProjectDTO class.
		else if(targetDomainObject instanceof ProjectDTO){			
			ProjectDTO project = (ProjectDTO) targetDomainObject;
			return project.getPrjid();			
		}
		
		// If target domain object isn't any of the following type exception is is thrown because
		// users who don't use supported credentials are usually hackers.
		else{
			throw new Exception("Target domain object credential type not supported");				
		}
		
	}
	
	public AppUserDTO FindAuthenticatedUser(Authentication authentication) throws Exception{
		
		// Authentication is the information stored into Spring security during authorization.
		String authenticatedUserName = authentication.getName();		
		AppUserDTO appuserdto;
		try {
			appuserdto = userService.findByName(authenticatedUserName);
		} catch (EntityNotFoundException e) {
			throw new Exception("User dosen't exist in database or being authorized");			
		}
		return appuserdto;
	}
		
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		
		System.out.println("Unsupported, need to be implemented to gain support");
		// TODO Auto-generated method stub
		return false;
	}

}
