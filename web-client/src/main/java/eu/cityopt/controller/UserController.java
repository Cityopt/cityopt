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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
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
//Contains Forms for UI
import eu.cityopt.forms.ExternParamIDForm;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
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
//Contains Forms for UI
import eu.cityopt.forms.ExternParamIDForm;
import eu.cityopt.model.UserGroupProject;
import eu.cityopt.repository.ProjectRepository;
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
import eu.cityopt.web.RoleForm;
import eu.cityopt.web.ScenarioParamForm;
import eu.cityopt.web.UnitForm;
import eu.cityopt.web.UserManagementForm;
import eu.cityopt.web.UserSession;
import eu.cityopt.service.ImportService;

/**
 * @author Olli Stenlund
 *
 */
@Controller
@SessionAttributes({
    "project", "scenario", "optimizationset", "scengenerator", "optresults",
    "usersession", "user", "version"})
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

    	@RequestMapping(method=RequestMethod.GET, value="/login")
		public String displayLoginPage(Map<String, Object> model){
    		     		   		 
    		 AppUserDTO user = new AppUserDTO();
    	     model.put("user", user);
    		 return "login";		 
		}
    	
    	private String getPrincipal(){
            String userName = null;
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof UserDetails) {
                userName = ((UserDetails)principal).getUsername();
            } else {
                userName = principal.toString();
            }
            return userName;
        }
    	
    	@RequestMapping(value="/logout",  method=RequestMethod.GET)
        public String logoutPage(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
    		    		
    		model.remove("project");
            model.remove("scenario");
            model.remove("optimizationset");
            model.remove("scengenerator");
            model.remove("optresults");
            model.remove("usersession");
            model.remove("user");
            request.getSession().invalidate();        
    		
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
    	
    	
    	@RequestMapping(value="/loginOK", method=RequestMethod.GET)
    	public String loginOK(Map<String, Object> model){
    		
    		AppUserDTO user = new AppUserDTO();
    		user.setName(this.getPrincipal());
    		model.put("user", user);

    		return  "start";
    	}

    
    @RequestMapping(value="index", method=RequestMethod.GET)
    public String getIndex(Map<String, Object> model) {
    	System.out.println("Index invoked");
        AppUserDTO user = new AppUserDTO();
        model.put("user", user);

        return "index";
    }

    
    //@ Markus Adding security feature:    
    @RequestMapping(value="index", method=RequestMethod.POST)
    public String getIndexPost(Map<String, Object> model, AppUserDTO userForm) {

        String version = appMetaData.getVersion();
        model.put("version", version);
        
               
       // BCryptPasswordEncoder passwordEnconder = new BCryptPasswordEncoder(12);       
       // Password dosen't help in trimming or does it?
        
        String username = userForm.getName();
        String password = userForm.getPassword();
       
        AppUserDTO user = null;

        try {
            user = userService.findByNameAndPassword(username, password);
            
            // ToDo Implement theese:            
            //user = userService.findByName(username);
            // String (encrypted password) getEncryptedPasswordByName(username);
            // passwordEnconder.matches(rawPassword, encodedPassword)
            
        } catch (EntityNotFoundException e) {
            System.out.println("User " + username + " not found");
        }

        if (user != null)
        {
            model.put("user", user);
            return "start";
        }
        else
        {
            model.put("errorMsg", "Login error");
            model.put("user",  new AppUserDTO());
            return "index";
        }
    }
    
    //@author Markus Turunen Usemanagement
    //--------------
    @Secured({"ROLE_Administrator"})
    @RequestMapping(value="usermanagement", method=RequestMethod.GET)
    public String getUserManagement(Map<String, Object> model) {

        AppUserDTO user = (AppUserDTO) model.get("user");      
        if (user != null){
            	initializeUserManagement(model);                
                return "usermanagement";
            }        
        return "error";
    }
    
    @RequestMapping(value="usermanagement",method=RequestMethod.POST)
    public String getEditUser(Map<String, Object> model,
    		UserManagementForm form) {    	
    	 
    	//	Test print of Form:
    		System.out.println("usemanagement: invoked");
    		System.out.println(form.getUser().keySet()+" "+form.getUser().values());    		
    		System.out.println(form.getPassword().keySet()+" "+form.getPassword().values());
    		System.out.println(form.getUserRole().keySet()+" "+form.getUserRole().values());
    		System.out.println(form.getProject().keySet()+" "+form.getProject().values());
    		System.out.println(form.getEnabled().keySet()+" "+form.getEnabled().values());
    		
    		Iterator<Integer> keySetIterator = form.getUser().keySet().iterator();
    		String username;
    		
    		while(keySetIterator.hasNext()){    		
    		Integer key = keySetIterator.next();
    			AppUserDTO user=null;
				try {user = userService.findByID(key);} catch (EntityNotFoundException e) 
				{	// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Handles user and password
    			user.setName(form.getUser().get(key).trim());
    			user.setPassword(form.getPassword().get(key).trim());    			
    			
    			// Set up Boolean Checkbox Bug fix Form Checkbox get nulls; 
    			if(form.getEnabled().get(key)!=null){
    				user.setEnabled(true);
    			}else{
    				user.setEnabled(false);
    			}    			    					   			
    			//userService.save(user);
    			try {userService.update(user);} catch (EntityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}    					
			initializeUserManagement(model);
			return "usermanagement";   	
    
    		} 
        
    //@author Markus Turunen
    // Initialize the UserManagementForms set up the model;
    // Made class to reduce repetition in my code.    
    public void initializeUserManagement(Map<String, Object> model){
    	
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
    }    
    
    

	// Form Factory: 
    public UserManagementForm CreateUsemanagementForm(List<AppUserDTO> users){    
   
    	UserManagementForm form = new UserManagementForm();
    	UserGroupProject usergroup; 
    	
	for (Iterator i = users.iterator(); i.hasNext(); ){	
		
		AppUserDTO appuser=(AppUserDTO) i.next();
		int id = appuser.getUserid();
		String name = appuser.getName();
		String password=  appuser.getPassword();		
		int ugpResult = 0;
		String project="";
		
		List <UserGroupProjectDTO> list = userGroupProjectService.findByUser(name);
			for(UserGroupProjectDTO ugp : list ){
				if(ugp.getProject()==null){
					ugpResult=ugp.getUsergroup().getUsergroupid();
					project += "null";
				}else{
				project += ugp.getProject().getName();
				}					
			}
				
		form.getProject().put(id, project);	
		form.getUserRole().put(id, ugpResult);		
		form.getUser().put(id,name);
		form.getPassword().put(id, password);		
		}
	
	return form;   
	}            	
    
    // Usergroup Help Methods;
    public UserGroupProjectDTO CreateUserGroupDTO(UserGroupDTO usergroup, 
    		ProjectDTO project, AppUserDTO appuser){    	
    	UserGroupProjectDTO userGroupDTO=new UserGroupProjectDTO();
    	userGroupDTO.setProject(project);
    	userGroupDTO.setProject(project);
    	userGroupDTO.setAppuser(appuser);    	
    	return userGroupDTO;
    }
    
    public void saveGroupProject(UserGroupProjectDTO userproject){    	
    	userGroupProjectService.save(userproject);
    }
    
    
    // Try to make something to print user's projects and Roles,
    public void UserProjectGroups(AppUserDTO user,UserGroupProjectDTO userGroup){
    	
    	
    	    	
    	
    	
    }
    
    @RequestMapping(value="createuser",method=RequestMethod.GET)
    public String getCreateUser(Map<String, Object> model) {       
        this.initializeUserManagement(model);        
        return "createuser";
    }
        //AppUserDTO user = new AppUserDTO();
        //List<UserGroupDTO> userGroups= userGroupService.findAll();
        //model.put("user", user);
       
    

    @RequestMapping(value="createuser", method=RequestMethod.POST)
    public String getCreateUserPost(UserForm userForm, Map<String, Object> model) {
    	 if (userForm.getName() != null && userForm.getPassword() != null)
         {
             AppUserDTO user = new AppUserDTO();
             user.setName(userForm.getName().trim());
             user.setPassword(userForm.getPassword().trim());
             user.setEnabled(userForm.getEnabled());
             user = userService.save(user);
             	   try {
             		   	userService.update(user);
             		   } 
             	   catch (EntityNotFoundException e) {
             		// TODO Auto-generated catch block e.printStackTrace();
             	   }
                     
             UserGroupProjectDTO usergroupDTO =new UserGroupProjectDTO();             
             usergroupDTO.setProject(null);
             usergroupDTO.setAppuser(user);
           
             int useroleid=userForm.getRole();
             UserGroupDTO newUser = null;
 			try {
 				newUser = userGroupService.findByID(useroleid);
 			} catch (EntityNotFoundException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
             usergroupDTO.setUsergroup(newUser);             
             userGroupProjectService.save(usergroupDTO);
             
             
             // Assign into initial asignment             
             UserGroupProjectDTO usergroup2 = usergroupDTO;
             String userProject = userForm.getProject();
             ProjectDTO project = projectService.findByName(userProject);	
             usergroup2.setProject(project);
             userGroupProjectService.save(usergroup2); 
             
             	   
             initializeUserManagement(model);           	  
         } 
         else{
         	return "createuser";
         }
        
         return "usermanagement"; 
     }
    
    @RequestMapping(value="edituser",method=RequestMethod.GET)
    public String getEditUser(Map<String, Object> model, 
            @RequestParam(value="userid", required=true) String userid) {
    	
    	this.InitiateEditUser(model, userid);    	
        return "edituser";
    }
      
    // Other classes going to use this service:
    public void InitiateEditUser(Map<String, Object> model, String userid){
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
    
    //user.setName(form.getUser());
    //List<InputParamValDTO> inputParamVals = inputParamValService.findByComponentAndScenario(nSelectedCompId, scenario.getScenid());
    //@RequestParam(value="userid", required=true) String userid
    	//for (InputParamValDTO inputParameterValue : inputParamVals) {
    	
    	/*
        int nUserId = 0;
        AppUserDTO user = null;
        nUserId = Integer.parseInt(userid);
        try {
            user = userService.findByID(nUserId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        model.put("user", user);
        List<UserGroupProjectDTO> listUserGroupProjects = userGroupProjectService.findByUser(nUserId);
        model.put("userRoles", listUserGroupProjects);

        return "edituser";
        */
   
    @RequestMapping(value="edituser", method=RequestMethod.POST)
	public String getEditUserPost(UserForm userForm, Map<String, Object> model,
		@RequestParam(value="userid", required=true) String userId) {

    	AppUserDTO user = this.ParseUserIDtoUser(userId);
    	
    	if (userForm.getName() != null)
		{
			user.setName(userForm.getName());
			userService.save(user);
		}

		List<AppUserDTO> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
    }
    
    
    @RequestMapping(value="removerole", method=RequestMethod.GET)
    public String RemoveRole(Map<String, Object> model,
    	@RequestParam(value="userid", required=true) String userid,
    	@RequestParam(value="projectid", required=true) String projectid) {
        //int nUserId = Integer.parseInt(userid); 
    	
    	// Test prints
    	//System.out.println("delete invoked");
    	//System.out.println(projectid);
    	//System.out.println(userid); 
    	
    	int useridn = Integer.parseInt(userid);
    	int usergroupprojectid = Integer.parseInt(projectid);
    	
    	try {
			userGroupProjectService.delete(usergroupprojectid);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   	
    	    	
    	
		this.InitiateEditUser(model, userid);				
		return "edituser";
    	}
      
       
    //Returns User from userDTO from raw input data        
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
    public String getAddRole(Map<String, Object> model, 
            @RequestParam(value="userid", required=true) String userid) {
        
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
            errorMsg = "User null";
        }

        if (userGroup == null) {
            errorMsg += "User group null";
        }

        if (project == null) {
            errorMsg = "Project null";
        }

        model.put("errorMsg", errorMsg);

        try {
            userService.addToUserGroupProject(user.getUserid(), userGroup.getUsergroupid(), project.getPrjid());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Link back to to previous page
        this.InitiateEditUser(model,userid);       
        return "edituser";
    }

    @RequestMapping(value="deleteuser", method=RequestMethod.GET)
    public String getDeleteUser(Map<String, Object> model, @RequestParam(value="userid") String userid) {
        int nUserId = Integer.parseInt(userid);

        if (nUserId >= 0)
        {
            AppUserDTO user = null;
            try {
                user = userService.findByID(nUserId);
            } catch (EntityNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                userService.delete(user.getUserid());
            } catch (EntityNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        List<AppUserDTO> users = userService.findAll();
        //model.addAttribute("users", users);
        this.initializeUserManagement(model);        
        return "usermanagement";
    }

    public boolean hasAdminRights(int nUserId, int nProjectId) {
        UserGroupProjectDTO userGroupProject = userGroupProjectService.findByUserAndProject(nUserId, nProjectId);

        if (userGroupProject != null && userGroupProject.getUsergroup().getName().equals("Administrator"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean hasExpertRights(int nUserId, int nProjectId) {
        UserGroupProjectDTO userGroupProject = userGroupProjectService.findByUserAndProject(nUserId, nProjectId);

        if (userGroupProject != null)
        {
            if (userGroupProject.getUsergroup().getName().equals("Administrator")
                    || userGroupProject.getUsergroup().getName().equals("Expert"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasStandardRights(int nUserId, int nProjectId) {
        UserGroupProjectDTO userGroupProject = userGroupProjectService.findByUserAndProject(nUserId, nProjectId);

        if (userGroupProject != null)
        {
            if (userGroupProject.getUsergroup().getName().equals("Administrator")
                    || userGroupProject.getUsergroup().getName().equals("Expert")
                    || userGroupProject.getUsergroup().getName().equals("Standard"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasGuestRights(int nUserId, int nProjectId) {
        UserGroupProjectDTO userGroupProject = userGroupProjectService.findByUserAndProject(nUserId, nProjectId);

        if (userGroupProject != null)
        {
            if (userGroupProject.getUsergroup().getName().equals("Administrator")
                    || userGroupProject.getUsergroup().getName().equals("Expert")
                    || userGroupProject.getUsergroup().getName().equals("Standard")
                    || userGroupProject.getUsergroup().getName().equals("Guest"))
            {
                return true;
            }
        }
        return false;
    }
}
