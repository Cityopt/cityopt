package eu.cityopt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.UserGroupDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.model.AppUser;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ProjectManagementService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.UserGroupProjectService;
import eu.cityopt.service.UserGroupService;

@Service
public class ProjectManagementServiceImpl implements ProjectManagementService {

	@Autowired
	ProjectService projectService;
	
	@Autowired
	UserGroupService userGroupService;
	
	@Autowired
	AppUserService userService;
	
	@Autowired
	UserGroupProjectService userGroupProjectService;
	
	@Transactional
	@Override
	public ProjectDTO createProjectWithAdminUser(ProjectDTO NewProject, String user) {
			
		try {
			ProjectDTO createdProject = projectService.save(NewProject, 0, 0);
			// Test: caused a overlapping project. and transaction failed.
						
			List<UserGroupDTO> userGroups = userGroupService.findByGroupName("ROLE_Administrator");
			if(userGroups.size()!=1){throw new RuntimeException("Insufficient usergroup size");}
			UserGroupDTO userGroup = userGroups.get(0);
			AppUserDTO userProjectAdmin = userService.findByName(user);			
			UserGroupProjectDTO userGroupProject= new UserGroupProjectDTO();

			userGroupProject.setAppuser(userProjectAdmin);
			userGroupProject.setProject(createdProject);
			userGroupProject.setUsergroup(userGroup);			
			userGroupProject.setVersion(0);	// ToDO: Check & Change if you must.		
			userGroupProjectService.save(userGroupProject);
			
			return createdProject;
				
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		// TODO Auto-generated method stub
		return null;
	}

}
