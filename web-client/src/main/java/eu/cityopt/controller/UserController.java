package eu.cityopt.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.script.ScriptException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.DTO.UserGroupDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.config.AppMetadata;
import eu.cityopt.sim.eval.Type;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.script.ScriptException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.DTO.UserGroupDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.config.AppMetadata;
import eu.cityopt.model.UserGroupProject;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.security.SecurityAuthorization;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.AprosService;
import eu.cityopt.service.ComponentInputParamDTOService;
import eu.cityopt.service.ComponentService;
import eu.cityopt.service.CopyService;
import eu.cityopt.service.DatabaseSearchOptimizationService;
import eu.cityopt.service.DecisionVariableService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamService;
import eu.cityopt.service.ExtParamValService;
import eu.cityopt.service.ExtParamValSetService;
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
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.PasswordForm;
import eu.cityopt.web.ProjectRole;
import eu.cityopt.web.RoleForm;
import eu.cityopt.web.ScenarioParamForm;
import eu.cityopt.web.UnitForm;
import eu.cityopt.web.UserForm;
import eu.cityopt.web.UserManagementForm;
import eu.cityopt.web.UserSession;
import eu.cityopt.service.ImportService;

/**
 * 
 * @author Markus Turunen  
 * My code is under Apache Licence (ASL) * This code library implement Spring Security 4.0.1 / April 23, 2015 
 * 
 * The Apache License (ASL) is a free software license written by the Apache Software Foundation (ASF).
 * The Apache License requires preservation of the copyright notice and disclaimer. 
 * Like other free software licenses, the license allows the user of the software the 
 * freedom to use the software for any purpose, to distribute it, to modify it, and to distribute modified 
 * versions of the software, under the terms of the license, without concern for royalties.
 * 
 * @author Olli Stenlund
 * This class contributes to user management of the CityOpt system.
 * 
 */

@Controller
@SessionAttributes({
    "project", "scenario", "optimizationset", "scengenerator", "optresults",
    "usersession", "user", "version", "activeblock", "page"})
public class UserController {

    @Autowired
    AppMetadata appMetaData;

    @Autowired
    ProjectService projectService; 

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ScenarioService scenarioService;

    @Autowired
    AppUserService userService;

    @Autowired
    UserGroupService userGroupService;

    @Autowired
    UserGroupProjectService userGroupProjectService;

    @Autowired
    ComponentService componentService;

    @Autowired
    ComponentInputParamDTOService componentInputParamService;

    @Autowired
    InputParameterService inputParamService;

    @Autowired
    InputParamValService inputParamValService;

    @Autowired
    ExtParamService extParamService;

    @Autowired
    ExtParamValService extParamValService;

    @Autowired
    ExtParamValSetService extParamValSetService;

    @Autowired
    MetricService metricService;

    @Autowired
    MetricValService metricValService;

    @Autowired
    UnitService unitService;

    @Autowired
    SimulationService simService;

    @Autowired
    SimulationResultService simResultService;

    @Autowired
    TimeSeriesService timeSeriesService;

    @Autowired
    TimeSeriesValService timeSeriesValService;

    @Autowired
    OutputVariableService outputVarService;

    @Autowired
    TypeService typeService;

    @Autowired
    CopyService copyService;

    @Autowired
    OptimizationSetService optSetService;

    @Autowired
    ObjectiveFunctionService objFuncService;

    @Autowired
    OptConstraintService optConstraintService;

    @Autowired
    OptSearchConstService optSearchService;

    @Autowired
    ScenarioGeneratorService scenGenService;

    @Autowired
    DecisionVariableService decisionVarService;

    @Autowired
    DatabaseSearchOptimizationService dbOptService;

    @Autowired
    ImportExportService importExportService;

    @Autowired
    ImportService importService;

    @Autowired
    MessageSource resource;
        
    @Autowired
    SecurityAuthorization securityAuthorization;
    
    @Autowired
    ControllerService controllerService;
    
    @Autowired
    OptionalValidatorFactoryBean validator;
    
    @RequestMapping(method=RequestMethod.GET, value="/accessDenied")
    public String AccessDenied(){
    	System.out.println("AccessDenied");
    	return "403";
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/login")
    public String displayLoginPage(Map<String, Object> model) {    		 		   		 
    	AppUserDTO user = new AppUserDTO();
    	model.put("user", user);
    	return "login";		 
	}

    public void initUserInfo(Map<String, Object> model) {
    	AppUserDTO user = null;
    	String userName = getPrincipal();
    	
    	try {
            user = userService.findByName(userName);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    	
    	model.put("user", user);

    	List<UserGroupProjectDTO> listUserGroupProjects = userGroupProjectService.findByUser(user.getUserid());
        model.put("userRoles", listUserGroupProjects);
        
        model.put("passwordForm", new PasswordForm());
    }
    
    public String getPrincipal() {
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails)principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }
    
    public void initializeUserManagement(Map<String, Object> model){
    	securityAuthorization.atLeastAdmin();   	
    	//Set up Variables 
    	List<AppUserDTO> users = userService.findAll();
        List<UserGroupDTO> userGroups= userGroupService.findAll();
        List<ProjectDTO> projects = projectService.findAll();        
        List<UserGroupProjectDTO> usergroupprojects= userGroupProjectService.findAll();
        UserForm userForm = new UserForm();        
    	UserManagementForm form = this.CreateUsemanagementForm(users);
    	    	  	
    	//Put Front controller using model attribute.
    	model.put("UserForm",userForm);
    	model.put("UserGroupProject", usergroupprojects);
    	model.put("UserManagementForm", form);	
        model.put("projects", projects);                
        model.put("userGroups", userGroups);
        model.put("users", users);
        List<UserGroupProjectDTO> listUserGroupProjects = userGroupProjectService.findAll();       
        model.put("userRoles", listUserGroupProjects);
        
        // Put a list of ProjectDTOs by User in a list form into model.
        this.FindUsersProjects(model);
    }    
  
    public UserManagementForm CreateUsemanagementForm(List<AppUserDTO> users) {    
    	securityAuthorization.atLeastAdmin();
    	// Set up initial variables.
    	UserManagementForm form = new UserManagementForm();
    	UserGroupProject usergroup;	
	    	
	    // This iterates all users and set up their information from database 
		for (Iterator i = users.iterator(); i.hasNext(); ){	
			
			AppUserDTO appuser=(AppUserDTO) i.next();
			int id = appuser.getUserid();
			String name = appuser.getName();
			String password=  appuser.getPassword();		
			int ugpResult = 0;
			String project="";
			
			// Find userGroup Project and set up user group id.
			List <UserGroupProjectDTO> list = userGroupProjectService.findByUser(name);
				for(UserGroupProjectDTO ugp : list ){
					if(ugp.getProject()==null){
						ugpResult=ugp.getUsergroup().getUsergroupid();
						project += "null";
					}else{
					project += ugp.getProject().getName();
					}					
				}
	
			// Set up the form for Spring From.
			form.getProject().put(id, project);	
			form.getUserRole().put(id, ugpResult);		
			form.getUser().put(id,name);
			form.getPassword().put(id, password);	
		}
	
		return form;   
	}            	
    
    //@author Markus Turunen: This Method Creates a UserGroupDTO -object based on the parameters given.
    public UserGroupProjectDTO CreateUserGroupDTO(UserGroupDTO usergroup, 
    	ProjectDTO project, AppUserDTO appuser){   
    	
    	securityAuthorization.atLeastAdmin();
    	UserGroupProjectDTO userGroupProject = new UserGroupProjectDTO();
    	userGroupProject.setProject(project);
    	userGroupProject.setAppuser(appuser);    	
    	return userGroupProject;
    }

    public void FindUsersProjects(Map<String, Object> model){   
    	securityAuthorization.atLeastAdmin();
    	List<AppUserDTO> users= userService.findAll();
    	ArrayList <List <ProjectDTO> > ListOfUserProjects = new ArrayList<List <ProjectDTO>>();
    	   	
    	for (int i=0; users.size() > i; i++) {
    		List<ProjectDTO> userProject = userGroupProjectService.findProjectsByUser(users.get(i).getUserid());
    		ListOfUserProjects.add(userProject);
    	}    	
    	model.put("UserProject",ListOfUserProjects);
    	
    	// TODO make parser in JSP-page to utilize this resource.
    	// I made this to display user projects in user management,
    	// could not figure out how to utilize it in JSP.
    }

    public void InitiateEditUser(Map<String, Object> model, String userid){
    	securityAuthorization.atLeastAdmin();
    	int nUserId = 0;
        AppUserDTO user = null;
        nUserId = Integer.parseInt(userid);        
        try {
            user = userService.findByID(nUserId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        List<ProjectDTO> projects = projectService.findAll();
        List<UserGroupDTO> userGroups= userGroupService.findAll();
        
        model.put("projects", projects);
        model.put("userGroups", userGroups);
        RoleForm form = new RoleForm();        
        model.put("RoleForm", form);
        model.put("user", user);
        List<UserGroupProjectDTO> listUserGroupProjects = userGroupProjectService.findByUser(nUserId);
        model.put("userRoles", listUserGroupProjects);
    }

    

    public AppUserDTO FindAuthenticatedUser(Authentication authentication) throws Exception{
		
		String authenticatedUserName = authentication.getName();			
		AppUserDTO appuserdto;
		try {
			appuserdto = userService.findByName(authenticatedUserName);
		} catch (EntityNotFoundException e) {
			throw new Exception("User doesn't exist in the database");			
		}
		return appuserdto;
	}
    	    
    @RequestMapping(value="/logout",  method=RequestMethod.GET)
    public String logoutPage(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    		    		
		controllerService.clearSession(model, request, true);       
		
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        if (aut != null) {
            SecurityContextLogoutHandler ctxLogOut = new SecurityContextLogoutHandler();
            ctxLogOut.logout(request, response, aut);
        }               
                        
        return "redirect:/login.html?logout";
    }
    
    @RequestMapping(value="/loginerror",  method=RequestMethod.GET)
    public String loginError(Map<String, Object> model) {
		return "redirect:/login.html?error";
    }
    
    //@author Markus Turunen When user is Authenticated into system the system redirect user to welcome screen.
    @PreAuthorize("hasAnyRole('ROLE_Administrator','ROLE_Expert','ROLE_Standard','ROLE_Guest')") 
    @RequestMapping(value="/loginOK", method=RequestMethod.GET)
    public String loginOK(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		// We check model because project doesn't exist yet.
		controllerService.clearSession(model, null, true);
		AppUserDTO user = new AppUserDTO();
		user.setName(getPrincipal());
		model.put("user", user);
    	
		String language = request.getLocale().getLanguage();
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        localeResolver.setLocale(request, response, StringUtils.parseLocaleString(language));

        controllerService.changeLanguage(model, language);
        model.put("activeblock", "project");
        model.remove("page");
        
    	return  "start";
	}
    
    //@author Markus Turunen This is the index page get method
    @RequestMapping(value="index", method=RequestMethod.GET)
    public String getIndex(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    	// We check model because project doesn't exist yet.
    	securityAuthorization.atLeastGuest_guest(model);  
    	
        AppUserDTO user = new AppUserDTO();
        model.put("user", user);

        return "login";
    }
    
    @RequestMapping(value="usermanagement", method=RequestMethod.GET)
    public String userManagement(Map<String, Object> model) {

    	securityAuthorization.atLeastAdmin();
    	model.put("activeblock", "settings");
    	model.put("page", "usermanagement");
        AppUserDTO user = (AppUserDTO) model.get("user");      
        
        if (user != null) {
        	initializeUserManagement(model);                
            return "usermanagement";
        }        
        
        return "error";
    }
    
    @RequestMapping(value="usermanagement",method=RequestMethod.POST)
    public String userManagementPost(Map<String, Object> model,
    		 UserManagementForm form, BindingResult bindingResult) {    	
//    	Test print of Form: uncomment if you need to see user information collected from form.
    	/*
    		System.out.println("usemanagement: invoked");
    		System.out.println(form.getUser().keySet()+" "+form.getUser().values());    		
    		System.out.println(form.getPassword().keySet()+" "+form.getPassword().values());
    		System.out.println(form.getUserRole().keySet()+" "+form.getUserRole().values());
    		System.out.println(form.getProject().keySet()+" "+form.getProject().values());
    		System.out.println(form.getEnabled().keySet()+" "+form.getEnabled().values());
    	*/
    	
		securityAuthorization.atLeastAdmin();    	
	
		// Iterator to handle form logic.
		Iterator<Integer> keySetIterator = form.getUser().keySet().iterator();
		String username;
		
		while(keySetIterator.hasNext()){ 
			Integer key = keySetIterator.next();    			
			AppUserDTO user=null;
			try {user = userService.findByID(key);} catch (EntityNotFoundException e) 
			{	// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Handles
			user.setName(form.getUser().get(key).trim());
			String passwordfield = form.getPassword().get(key).trim();
			user.setPassword(passwordfield);
			
			validator.validate(user, bindingResult);
			if (bindingResult.hasErrors()){ 
				initializeUserManagement(model);
				model.put("bindingError", true);
				return "usermanagement";
			}
   		}
    		
		keySetIterator = form.getUser().keySet().iterator();    		
		
		while(keySetIterator.hasNext()){    		
		Integer key = keySetIterator.next();
			
			// Finds ID for identifying element.
			AppUserDTO user=null;
			try {user = userService.findByID(key);} catch (EntityNotFoundException e) 
			{	// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Handles
			user.setName(form.getUser().get(key).trim());
			
			// Password and it's encryption
			
			String passwordfield = form.getPassword().get(key).trim();
			
			if (!(user.getPassword().equals(passwordfield))){
				// uncrypted set method.
				//user.setPassword(form.getPassword().get(key).trim());
				
				BCryptPasswordEncoder passwordEnconder = new BCryptPasswordEncoder(12);
                String hashedPassword = passwordEnconder.encode(passwordfield);
                user.setPassword(hashedPassword);
			}
			
			// Set up Boolean Checkbox Bug fix Form Checkbox get nulls; 
			if(form.getEnabled().get(key) != null){
				user.setEnabled(true);
			}else{
				user.setEnabled(false);
			}    			    					   			

			try {
				userService.update(user);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
    	 }
		// Set up the user management page again with modified fields.
		initializeUserManagement(model);
		return "usermanagement";   	
	} 
    
    @RequestMapping(value="userinfo", method=RequestMethod.GET)
    public String userInfo(Map<String, Object> model) {
    	
    	ProjectDTO project = controllerService.GetProject(model);
    	securityAuthorization.atLeastStandard();

    	model.put("activeblock", "settings");
    	model.put("page", "userinfo");
        
    	initUserInfo(model);
        
        return "userinfo";
    }

    public void initProjectRoles(Map<String, Object> model, ProjectDTO project) 
    {
    	ArrayList<AppUserDTO> users = (ArrayList<AppUserDTO>) userService.findAll();
    	Iterator<AppUserDTO> iter = users.iterator();
    	ArrayList<ProjectRole> projectRoles = new ArrayList<ProjectRole>();
    	
    	while (iter.hasNext())
    	{
    		AppUserDTO user = iter.next();
    		ProjectRole role = new ProjectRole();
    		role.setUser(user);
    		role.setProject(project);
    		
    		UserGroupProjectDTO userGroupProject = userGroupProjectService.findByUserAndProject(user.getUserid(), project.getPrjid());
			UserGroupDTO userGroup = new UserGroupDTO();

    		if (userGroupProject != null)
    		{
    			userGroup = userGroupProject.getUsergroup();
    		}
    		
    		role.setProjectRole(userGroup.getName());
    		projectRoles.add(role);
    	}
    	
    	model.put("projectRoles", projectRoles);
    }
    
    @RequestMapping(value="projectroles", method=RequestMethod.GET)
    public String projectRoles(Map<String, Object> model) {
    	
    	ProjectDTO project = controllerService.GetProject(model);
    	
    	if (project == null)
    	{
    		return "error";
    	}
    	
        securityAuthorization.atLeastExpert_admin(project);

    	model.put("activeblock", "settings");
    	model.put("page", "projectroles");
        
    	initProjectRoles(model, project);
    	
        return "projectroles";
    }

    @RequestMapping(value="changeprojectrole", method=RequestMethod.GET)
    public String changeProjectRole(Map<String, Object> model,
        @RequestParam(value="userid", required=true) String userid,
        @RequestParam(value="role", required=true) String role) 
    {
    	ProjectDTO project = controllerService.GetProject(model);
    	
    	if (project == null)
    	{
    		return "error";
    	}
    	
    	securityAuthorization.atLeastExpert_admin(project);

    	model.put("activeblock", "settings");
    	model.put("page", "projectroles");
        
    	int nUserId = Integer.parseInt(userid);
    	AppUserDTO user = null;
    	
		try {
			user = userService.findByID(nUserId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
    	
    	int nRoleId = 4;
		
		if (role.equals("admin")) {
			nRoleId = 1;
		} else if (role.equals("expert")) {
			nRoleId = 2;
		} else if (role.equals("standard")) {
			nRoleId = 3;
		} else if (role.equals("guest")) {
			nRoleId = 4;
		}
		
    	UserGroupProjectDTO userGroupProject = userGroupProjectService.findByUserAndProject(nUserId, project.getPrjid());
    	
    	if (userGroupProject != null)
    	{
            UserGroupDTO userGroup = null;
            
			try {
				userGroup = userGroupService.findByID(nRoleId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
            
    		userGroupProject.setUsergroup(userGroup);
    		userGroupProjectService.save(userGroupProject);
    	}
    	else
    	{
    		userGroupProject = new UserGroupProjectDTO();
    		userGroupProject.setProject(project);
    		userGroupProject.setAppuser(user);
    		
            UserGroupDTO userGroup = null;
            
			try {
				userGroup = userGroupService.findByID(nRoleId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
            userGroupProject.setUsergroup(userGroup);
    		
            userGroupProjectService.save(userGroupProject);
    	}
    	
    	initProjectRoles(model, project);
    	
        return "projectroles";
    }

    @RequestMapping(value="createuser",method=RequestMethod.GET)
    public String createUser(Map<String, Object> model) {
    	
    	securityAuthorization.atLeastAdmin();    	
    	UserForm UserForm = new UserForm();
        model.put("userForm", UserForm);        
        initializeUserManagement(model);      
        
        return "createuser";
    }
    
    @RequestMapping(value="createuser", method=RequestMethod.POST)
    public String createUserPost(@Validated @ModelAttribute("UserForm") UserForm userForm, 
    	BindingResult bindingResult, Map<String, Object> model,
    	HttpServletRequest request) {    	    	
    	 
    	securityAuthorization.atLeastAdmin();
    	
    	try {  		
    		if (bindingResult.hasErrors()) {
    			throw new Exception();
    			// initializeUserManagement(model);    	 
    		}
    	 
    		if (userForm.getName() != null && userForm.getPassword() != null)
    		{
    			AppUserDTO user = new AppUserDTO();
    			user.setName(userForm.getName().trim()); 
	            user.setEnabled(userForm.getEnabled());
	            user.setPassword(userForm.getPassword().trim());
	             
	            // Validates the user according to UserDTO validation
	            validator.validate(user, bindingResult);
	            if (bindingResult.hasErrors()){
	            	throw new Exception();
	            }
	             
	            BCryptPasswordEncoder passwordEnconder = new BCryptPasswordEncoder(12);
	            String hashedPassword = passwordEnconder.encode(user.getPassword());
	            user.setPassword(hashedPassword);
	                          
	            try {
	            	user = userService.save(user);
	            } catch (ObjectOptimisticLockingFailureException e){
	    			model.put("error", controllerService.getMessage("project_updated", request));
	    		}
	            
	            try {
	            	userService.update(user);
	            } 
	            catch (EntityNotFoundException e) {
	            	e.printStackTrace();
	            }
	                     
	            UserGroupProjectDTO usergroupProject = new UserGroupProjectDTO();             
	            usergroupProject.setProject(null);
	            usergroupProject.setAppuser(user);
	           
	            int roleid = userForm.getRole();
	            UserGroupDTO userGroup = userGroupService.findByID(roleid);
	            usergroupProject.setUsergroup(userGroup);
	            
	            try {
	            	userGroupProjectService.save(usergroupProject);
	            } catch (ObjectOptimisticLockingFailureException e){
	    			model.put("error", controllerService.getMessage("project_updated", request));
	    		}
                          	   
	            initializeUserManagement(model);           	  
    		} 
    		else {
    			return "createuser";
    		}
    	} catch(Exception e) {
    		List<UserGroupDTO> userGroups= userGroupService.findAll();
    	    List<ProjectDTO> projects = projectService.findAll();
    	    model.put("projects", projects);                
    	    model.put("userGroups", userGroups);    		
    		return "createuser";
    	}
        
         return "usermanagement"; 
     }
    
    @RequestMapping(value="edituser",method=RequestMethod.GET)
    public String editUser(Map<String, Object> model, 
        @RequestParam(value="userid", required=true) String userid) {
    	securityAuthorization.atLeastAdmin();
    	
    	int nUserId = 0;
        AppUserDTO user = null;
        nUserId = Integer.parseInt(userid);        
        
        try {
            user = userService.findByID(nUserId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        
        AppUserDTO newUser = new AppUserDTO();
        newUser.setName(user.getName());
        newUser.setUserid(user.getUserid());
        
        model.put("user", newUser);
        
        return "edituser";
    }

    @RequestMapping(value="edituser", method=RequestMethod.POST)
	public String editUserPost(UserForm userForm, Map<String, Object> model,
		@RequestParam(value="userid", required=true) String userId,
		HttpServletRequest request) {
    	
    	securityAuthorization.atLeastAdmin();
    	AppUserDTO user = this.ParseUserIDtoUser(userId);
    	
    	if (userForm.getPassword() != null)
		{
    		BCryptPasswordEncoder passwordEnconder = new BCryptPasswordEncoder(12);
            String hashedPassword = passwordEnconder.encode(userForm.getPassword());
            user.setPassword(hashedPassword);
            
            try {
            	userService.save(user);
            } catch (ObjectOptimisticLockingFailureException e) {
    			model.put("error", controllerService.getMessage("project_updated", request));
    		}
		}

		List<AppUserDTO> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
    }

    @RequestMapping(value="confirmpassword",method=RequestMethod.POST)
    public String changePassword(Map<String, Object> model, 
        @RequestParam(value="userid", required=true) String userId,
        PasswordForm passwordForm, HttpServletRequest request) {
    	
    	securityAuthorization.atLeastStandard();
    	AppUserDTO user = this.ParseUserIDtoUser(userId);
    	
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
		
		if (passwordEncoder.matches(passwordForm.getOldPassword(), user.getPassword()))
		{
	    	AppUserDTO newUser = new AppUserDTO();
	        newUser.setName(user.getName());
	        newUser.setUserid(user.getUserid());
	        model.put("user", newUser);
	        
	        return "changepassword";
		}
		else
		{
			model.put("error", controllerService.getMessage("wrong_password", request));
			initUserInfo(model);
			return "userinfo";
		}
    }

    @RequestMapping(value="changepassword", method=RequestMethod.POST)
	public String changePasswordPost(UserForm userForm, Map<String, Object> model,
		@RequestParam(value="userid", required=true) String userId,
		HttpServletRequest request) {
    	
    	securityAuthorization.atLeastStandard();
    	AppUserDTO user = this.ParseUserIDtoUser(userId);
    	String password = userForm.getPassword();
    	
    	if (password != null)
		{
    		if (password.length() < 5)
    		{
    			model.put("error", controllerService.getMessage("password_too_short", request));

    			AppUserDTO newUser = new AppUserDTO();
    	        newUser.setName(user.getName());
    	        newUser.setUserid(user.getUserid());
    	        model.put("user", newUser);

    			return "changepassword";
    		}

    		BCryptPasswordEncoder passwordEnconder = new BCryptPasswordEncoder(12);
            String hashedPassword = passwordEnconder.encode(userForm.getPassword());
            user.setPassword(hashedPassword);
            
            try {
            	userService.save(user);
        		model.put("info", controllerService.getMessage("password_changed", request));
            } catch (ObjectOptimisticLockingFailureException e) {
    			model.put("error", controllerService.getMessage("project_updated", request));
    		}
		}

		initUserInfo(model);
		return "userinfo";
    }
    
    @RequestMapping(value="editroles", method=RequestMethod.GET)
    public String editRoles(Map<String, Object> model, 
    	@RequestParam(value="userid", required=true) String userid) {
    	securityAuthorization.atLeastAdmin();
    	InitiateEditUser(model, userid);    	
        return "editroles";
    }

    @RequestMapping(value="editroles", method=RequestMethod.POST)
	public String editRolesPost(UserForm userForm, Map<String, Object> model,
		@RequestParam(value="userid", required=true) String userId) {
    	
    	securityAuthorization.atLeastAdmin();
    	AppUserDTO user = this.ParseUserIDtoUser(userId);
    	
		List<AppUserDTO> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
    }

    @RequestMapping(value="removerole", method=RequestMethod.GET)
    public String RemoveRole(Map<String, Object> model,
    	@RequestParam(value="userid", required=true) String userid,
    	@RequestParam(value="ugpid", required=true) String ugpid) {
    	
    	securityAuthorization.atLeastAdmin();
    	
    	int useridn = Integer.parseInt(userid);
    	int usergroupprojectid = Integer.parseInt(ugpid);
    	
    	try {
			userGroupProjectService.delete(usergroupprojectid);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}   	
    	
    	InitiateEditUser(model, userid);				
		return "editroles";
   	}
             
    //@author Markus Turunen: Returns User from userDTO based from raw input data        
    public AppUserDTO ParseUserIDtoUser(String userid){    	
    	int nUserId = Integer.parseInt(userid);
        AppUserDTO user = null;
        try {
            user = userService.findByID(nUserId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    	return user;    	
    }
    
    @RequestMapping(value="createrole", method=RequestMethod.GET)
    public String createRole(Map<String, Object> model, 
        @RequestParam(value="userid", required=true) String userid) {
        
    	securityAuthorization.atLeastAdmin();
    	AppUserDTO user=ParseUserIDtoUser(userid);
    	List<ProjectDTO> projects = projectService.findAll();
        model.put("projects", projects);
        model.put("user", user);
        UserGroupProjectDTO role = new UserGroupProjectDTO();
        model.put("role",  role);
        
        return "createrole";
    }
    
    @RequestMapping(value="createrole", method=RequestMethod.POST)
    public String getCreateRolePost(Map<String, Object> model, HttpServletRequest request, 
        @RequestParam(value="userid", required=true) String userid) {
    	
    	securityAuthorization.atLeastAdmin();
    	AppUserDTO user= ParseUserIDtoUser(userid);
        model.put("user", user);
        String role = request.getParameter("roleType");
        UserGroupDTO userGroup = null;

        // TODO Change hard coded strings to final strings for the controller 
        if (role.equals("Administrator")) {
            List<UserGroupDTO> userGroups = userGroupService.findByGroupNameContaining("Administrator");
            userGroup = userGroups.get(0);
        } else if (role.equals("Expert")) {
            List<UserGroupDTO> userGroups = userGroupService.findByGroupNameContaining("Expert");
            userGroup = userGroups.get(0);
        } else if (role.equals("Standard")) {
            List<UserGroupDTO> userGroups = userGroupService.findByGroupNameContaining("Standard");
            userGroup = userGroups.get(0);
        } else if (role.equals("Guest")) {
            List<UserGroupDTO> userGroups = userGroupService.findByGroupNameContaining("Guest");
            userGroup = userGroups.get(0);
        }

        String roleProjectId = request.getParameter("roleProjectId");
        int nProjectId = Integer.parseInt(roleProjectId);
        ProjectDTO project = null;

        try {
            project = projectService.findByID(nProjectId);
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }
        String errorMsg = "";

        if (user == null) {
            errorMsg = controllerService.getMessage("add_user", request);
        }

        if (userGroup == null) {
            errorMsg += controllerService.getMessage("add_user_group", request);
        }

        if (project == null) {
            errorMsg = controllerService.getMessage("add_project", request);
        }

        model.put("errorMsg", errorMsg);

        try {
            userService.addToUserGroupProject(user.getUserid(), userGroup.getUsergroupid(), project.getPrjid());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Link back to to previous page
        InitiateEditUser(model,userid);       
        return "editroles";
    }

    @RequestMapping(value="deleteuser", method=RequestMethod.GET)
    public String getDeleteUser(Map<String, Object> model, @RequestParam(value="userid") String userid) {
    	
    	securityAuthorization.atLeastAdmin();
    	int nUserId = Integer.parseInt(userid);
        if (nUserId >= 0)
        {
            AppUserDTO user= controllerService.FindUserbyUserID(nUserId);
            try {
                userService.delete(user.getUserid());
            } catch (EntityNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Setup user management page with changes
        List<AppUserDTO> users = userService.findAll();
        //model.addAttribute("users", users);
        initializeUserManagement(model);        
        return "usermanagement";
    }
}
