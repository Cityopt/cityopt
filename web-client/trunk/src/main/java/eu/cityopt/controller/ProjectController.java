package eu.cityopt.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.collection.internal.PersistentSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.cityopt.model.AppUser;
import eu.cityopt.model.Component;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.AppUserServiceImpl;
import eu.cityopt.service.ComponentServiceImpl;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ProjectServiceImpl;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.ScenarioServiceImpl;

@Controller
@SessionAttributes("project")
public class ProjectController {

//	Project project;
//	Project newProject;
	AppUser user;
	UserForm userForm;
	Scenario scenario;
	Scenario newScenario;
	Component component;
	//InputParameter newInputParameter;
	
	@Autowired
	ProjectServiceImpl projectService; 

	@Autowired
	AppUserServiceImpl userService;
	
	@Autowired
	ScenarioServiceImpl scenarioService; 

	@Autowired
	ComponentServiceImpl componentService;
	
	@RequestMapping(value="getProjects",method=RequestMethod.GET)
	public String getGoalReports(Model model) {
		List<Project> projects = projectService.findAll();
		model.addAttribute("projects",projects);
		
		return "getProjects";
	}	

	@RequestMapping(value="createproject", method=RequestMethod.GET)
	public String getCreateProject(Map<String, Object> model) {
		Project newProject = new Project();
		model.put("project", newProject);
		return "createproject";
	}

	@RequestMapping(value="openproject", method=RequestMethod.GET)
	public String getStringProjects(Map<String, Object> model)
	{
		List<Project> projects = projectService.findAll();
		model.put("projects", projects);
	
		return "openproject";
	}	

	@RequestMapping(value="editproject", method=RequestMethod.GET)
	public String getEditProject(Map<String, Object> model, @RequestParam(value="prjid", required=false) String prjid) {
		if (prjid != null)
		{
			Project project = projectService.findByID(Integer.parseInt(prjid));
			model.put("project", project);

			//projectForm = new ProjectForm();
			//projectForm.setProjectName(project.getName());
			//projectForm.setProjectCreator("" + project.getCreatedby());
			//projectForm.setLocation(project.getLocation());
			//projectForm.setDate(project.getCreatedon().toString());
			//projectForm.setDescription(project.getName());
		}
		else if (!model.containsKey("project"))
		{
			Project newProject = new Project();
			model.put("project", newProject);
			return "createproject";
		}

		return "editproject";
	}

	@RequestMapping(value="editproject", method=RequestMethod.POST)
	public String getEditProjectPost(Project project, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {
	
		if (project != null && action != null)
		{
			if (action.equals("create"))
			{
			}
			else if (action.equals("update"))
			{
			}
			
			//project.setName(projectForm.getProjectName());
			project.getPrjid();
			projectService.save(project);
			model.put("project", project);
		}
		
		return "editproject";
	}

	@RequestMapping(value="closeproject", method=RequestMethod.GET)
	public String getCloseProjects(Map<String, Object> model, HttpServletRequest request)
	{
		model.remove("project");
		request.getSession().removeAttribute("project");
		request.getSession().invalidate();
		return "start";
	}	

	@RequestMapping(value="index",method=RequestMethod.GET)
	public String getIndex(Model model) {
	
		return "index";
	}

	@RequestMapping(value="start",method=RequestMethod.GET)
	public String getStart(Model model){
	
		return "start";
	}

	@RequestMapping(value="deleteproject",method=RequestMethod.GET)
	public String getDeleteProject(Model model, @RequestParam(value="prjid", required=false) String prjid){
		if (prjid != null)
		{
			Project tempProject = projectService.findByID(Integer.parseInt(prjid));
			try {
				projectService.delete(tempProject);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<Project> projects = projectService.findAll();
		model.addAttribute("projects",projects);

		return "deleteproject";
	}

	@RequestMapping(value="createscenario",method=RequestMethod.GET)
	public String getCreateScenario(Map<String, Object> model) {
		newScenario = new Scenario();
		model.put("scenario", newScenario);
		return "createscenario";
	}

	@RequestMapping(value="openscenario",method=RequestMethod.GET)
	public String getOpenScenario (Map<String, Object> model, @RequestParam(value="scenarioid", required=false) String scenarioid)
	{
		Project project = (Project) model.get("project");
		
		if (project == null)
		{
			return "createscenario";
		}
		
		Set<Scenario> projectScenarios = project.getScenarios();
		model.put("scenarios", projectScenarios);

		if (scenarioid != null)
		{
			scenario = scenarioService.findByID(Integer.parseInt(scenarioid));
			
			model.put("scenario", scenario);
			
			return "editscenario";
		}

		return "openscenario";
	}

	@RequestMapping(value="editscenario",method=RequestMethod.GET)
	public String getEditScenario (Map<String, Object> model) {
		
		if (scenario != null)
		{
			model.put("scenario", scenario);
		}
		else
		{
			newScenario = new Scenario();
			model.put("scenario", newScenario);
			return "createscenario";
		}
		
		return "editscenario";
	}

	@RequestMapping(value="editscenario",method=RequestMethod.POST)
	public String getEditScenarioPost(Scenario newScenario, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {

		if (model.containsKey("project") && newScenario != null && action != null)
		{
			Project project = (Project) model.get("project");
			scenario = newScenario;
			
			scenario.setProject(project);
			
			if (action.equals("create"))
			{
			}
			else if (action.equals("update"))
			{
			}
			
			scenarioService.save(scenario);
		}
		else
		{
			//project null
		}
			
		model.put("scenario", scenario);
		return "editscenario";
	}
	
	@RequestMapping(value="deletescenario",method=RequestMethod.GET)
	public String getDeleteScenario(Model model, @RequestParam(value="scenarioid", required=false) String scenarioid){
		//List<Scenario> scenarios = scenarioService.findAllScenarios();
		//model.addAttribute("scenarios",scenarios);
	
		if (scenarioid != null)
		{
			Scenario tempScenario = scenarioService.findByID(Integer.parseInt(scenarioid));
			try {
				scenarioService.delete(tempScenario);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<Scenario> scenarios = scenarioService.findAll();
		model.addAttribute("scenarios", scenarios);

		return "deletescenario";
	}

	
	@RequestMapping(value="usermanagement", method=RequestMethod.GET)
	public String getUserManagement(Model model){
		List<AppUser> users = userService.findAll();
		model.addAttribute("users", users);
	
		return "usermanagement";
	}

	@RequestMapping(value="createuser",method=RequestMethod.GET)
	public String getCreateUser(Map<String, Object> model) {
		userForm = new UserForm();
		model.put("userForm", userForm);
	
		return "createuser";
	}

	@RequestMapping(value="createuser", method=RequestMethod.POST)
	public String getCreateUserPost(UserForm userForm, Map<String, Object> model) {
		if (userForm.getName() != null)
		{
			AppUser user = new AppUser();
			user.setName(userForm.getName());
			user.getUserid();
			userService.save(user);
		}

		List<AppUser> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
	}

	@RequestMapping(value="edituser",method=RequestMethod.GET)
	public String getEditUser(Model model, @RequestParam(value="userid", required=true) String userid) {
		int nUserId = Integer.parseInt(userid);
		
		user = userService.findByID(nUserId);
		userForm = new UserForm();
		userForm.setName(user.getName());
		model.addAttribute("userForm", userForm);

		return "edituser";
	}

	@RequestMapping(value="edituser", method=RequestMethod.POST)
	public String getEditUserPost(UserForm userForm, Map<String, Object> model) {
		if (userForm.getName() != null)
		{
			user.setName(userForm.getName());
			userService.save(user);
		}

		List<AppUser> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
	}
	
	@RequestMapping(value="deleteuser", method=RequestMethod.GET)
	public String getDeleteUser(Model model, @RequestParam(value="userid") String userid){
		int nUserId = Integer.parseInt(userid);
		
		if (nUserId >= 0)
		{
			AppUser user = userService.findByID(nUserId);
			try {
				userService.delete(user);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<AppUser> users = userService.findAll();
		model.addAttribute("users", users);

		return "usermanagement";
	}
	
	@RequestMapping(value="viewchart",method=RequestMethod.GET)
	public String getViewChart(Model model){
	
		return "viewchart";
	}
	
	@RequestMapping(value="viewtable",method=RequestMethod.GET)
	public String getViewTable(Model model){
	
		return "viewtable";
	}
	
	@RequestMapping(value="coordinates",method=RequestMethod.GET)
	public String getCoordinates(Model model){
	
		return "coordinates";
	}
	
	@RequestMapping(value="databaseoptimization",method=RequestMethod.GET)
	public String getDatabaseOptimization(Model model){
	
		return "databaseoptimization";
	}
	
	@RequestMapping(value="openoptimizationset",method=RequestMethod.GET)
	public String getOpenOptimizationSet(Model model){
	
		return "openoptimizationset";
	}

	@RequestMapping(value="deleteoptimizationset",method=RequestMethod.GET)
	public String getDeleteOptimizationSet(Model model){
	
		return "deleteoptimizationset";
	}

	@RequestMapping(value="outputvariables",method=RequestMethod.GET)
	public String getOutputVariables(Map<String, Object> model){
		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		Set<Component> projectComponents = project.getComponents();
		
		if (projectComponents != null && projectComponents.size() > 0)
		{
			model.put("components", projectComponents);
		}
	
		return "outputvariables";
	}
	
	@RequestMapping(value="runmultiscenario",method=RequestMethod.GET)
	public String getRunMultiScenario(Model model){
	
		return "runmultiscenario";
	}
	
	@RequestMapping(value="runmultioptimizationset",method=RequestMethod.GET)
	public String getRunMultiOptimizationSet(Model model){
	
		return "runmultioptimizationset";
	}

	@RequestMapping(value="metricdefinition",method=RequestMethod.GET)
	public String getMetricDefinition(Model model){
	
		return "metricdefinition";
	}

	@RequestMapping(value="projectparameters", method=RequestMethod.GET)
	public String getProjectParameters(Map<String, Object> model){
		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		Set<Component> setComponents = project.getComponents();
		List<Component> listComponents = new ArrayList<Component>();
        Iterator<Component> iterator = setComponents.iterator();
        
        while(iterator.hasNext())
        {
               Component cmp = iterator.next();
               listComponents.add(cmp);
               cmp.getName();
        }

		if (listComponents != null && listComponents.size() > 0)
		{
			model.put("components", listComponents);
		}
		
		return "projectparameters";
	}
	
	@RequestMapping(value="createcomponent", method=RequestMethod.GET)
	public String getCreateComponent(Model model){

		Component newComponent = new Component();
		model.addAttribute("component", newComponent);
		
		return "createcomponent";
	}

	@RequestMapping(value="createcomponent", method=RequestMethod.POST)
	public String getCreateComponentPost(Component component, Map<String, Object> model){
		Project project = (Project) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		component.setProject(project);
		componentService.save(component);
		return "projectparameters";
	}

	@RequestMapping(value="editcomponent", method=RequestMethod.GET)
	public String getEditComponent(Model model, @RequestParam(value="componentid", required=true) String componentid) {
		int nCompId = Integer.parseInt(componentid);
		component = componentService.findByID(nCompId);
		model.addAttribute("component", component);
		
		return "editcomponent";
	}

	@RequestMapping(value="createinputparameter", method=RequestMethod.GET)
	public String getCreateInputParameter(Map<String, Object> model){
		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}

		InputParameter newInputParameter = new InputParameter();
		model.put("inputParamForm", newInputParameter);
		
		return "createcomponent";
	}

	@RequestMapping(value="createinputparameter", method=RequestMethod.POST)
	public String getCreateInputParamPost(InputParameter inputParam, Map<String, Object> model){
		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}

		
		return "projectparameters";
	}
}
