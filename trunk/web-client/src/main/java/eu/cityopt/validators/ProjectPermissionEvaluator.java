package eu.cityopt.validators;

import java.io.Serializable;
import java.util.Date;

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
			
			
			int userID = appuserdto.getUserid();					
			UserGroupProjectDTO usergroupprojectdto = userGroupProjectService.findByUserAndProject(userID, projectIDn);
			if(usergroupprojectdto==null){
				return false;
			}			
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

		if ( targetDomainObject instanceof String){				
			String projectID = (String) targetDomainObject;	
			int projectIDn= Integer.parseInt(projectID);
			return projectIDn;
		}else if(targetDomainObject instanceof Integer){
			int projectIDn= (int) targetDomainObject;
			return projectIDn;
		}
		else{
			throw new Exception("Target domain object credential type not supported");				
		}
	}
	
	public AppUserDTO FindAuthenticatedUser(Authentication authentication) throws Exception{
				
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
