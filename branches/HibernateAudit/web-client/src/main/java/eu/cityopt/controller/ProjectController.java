package eu.cityopt.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cityopt.model.AppUser;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioService;

@Controller
public class ProjectController {

	ProjectForm projectForm;
	ProjectForm newProjectForm;
	Project project;
	AppUser user;
	UserForm userForm;
	Scenario scenario;
	Scenario newScenario;
	
	@Autowired
	ProjectService projectService; 

	@Autowired
	AppUserService userService;
	
	@Autowired
	ScenarioService scenarioService; 

	@RequestMapping(value="getProjects",method=RequestMethod.GET)
	public String getGoalReports(Model model) {
		List<Project> projects = projectService.findAll();
		model.addAttribute("projects",projects);
		
		return "getProjects";
	}	

	@RequestMapping(value="createproject", method=RequestMethod.GET)
	public String getCreateProject(Map<String, Object> model) {
		newProjectForm = new ProjectForm();
		model.put("projectForm", newProjectForm);
		return "createproject";
	}

	@RequestMapping(value="createscenario",method=RequestMethod.GET)
	public String getCreateScenario(Map<String, Object> model) {
		newScenario = new Scenario();
		model.put("scenario", newScenario);
		return "createscenario";
	}

	@RequestMapping(value="openproject", method=RequestMethod.GET)
	public String getStringProjects(Map<String, Object> model, @RequestParam(value="prjid", required=false) String prjid)
	{
		List<Project> projects = projectService.findAll();
		model.put("projects", projects);
	
		if (prjid != null)
		{
			project = projectService.findByID(Integer.parseInt(prjid));
			
			projectForm = new ProjectForm();
			projectForm.setProjectName(project.getName());
			projectForm.setProjectCreator("" + project.getCreatedby());
			projectForm.setLocation(project.getLocation());
			//projectForm.setDate(project.getCreatedon().toString());
			//projectForm.setDescription(project.getName());
			
			model.put("projectForm", projectForm);
			
			return "editproject";
		}
		
		return "openproject";
	}	

	@RequestMapping(value="editproject", method=RequestMethod.GET)
	public String getEditProject(Map<String, Object> model) {
		if (projectForm == null)
		{
			projectForm = new ProjectForm();
		}
		
		model.put("projectForm", projectForm);
		return "editproject";
	}

	@RequestMapping(value="editproject", method=RequestMethod.POST)
	public String getEditProjectPost(ProjectForm form, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {
		newProjectForm = form;
		
		if (newProjectForm != null && action != null)
		{
			projectForm = newProjectForm;

			if (action.equals("create"))
			{
				project = new Project();
				project.getPrjid();
				project.setName(projectForm.getProjectName());
				//	...
			}
			else if (action.equals("update"))
			{
			}
			
			project.setName(projectForm.getProjectName());
			projectService.save(project);
		}
		
		model.put("projectForm", projectForm);
		return "editproject";
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

	@RequestMapping(value="openscenario",method=RequestMethod.GET)
	public String getOpenScenario (Map<String, Object> model, @RequestParam(value="scenarioid", required=false) String scenarioid)
	{
		List<Scenario> scenarios = scenarioService.findAll();
		model.put("scenarios", scenarios);
	
		if (scenarioid != null)
		{
			scenario = scenarioService.findByID(Integer.parseInt(scenarioid));
			
			model.put("scenario", scenario);
			
			return "editscenario";
		}

		//List<Scenario> scenarios = scenarioService.findAllScenarios();
		//model.addAttribute("scenarios",scenarios);
	
		return "openscenario";
	}

	@RequestMapping(value="editscenario",method=RequestMethod.GET)
	public String getEditScenario (Map<String, Object> model){
		model.put("scenario", scenario);
		
		return "editscenario";
	}

	@RequestMapping(value="editscenario",method=RequestMethod.POST)
	public String getEditScenarioPost(Scenario newScenario, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {

		if (project != null && newScenario != null && action != null)
		{
			scenario = newScenario;
			
			scenario.setProject(project);
			
			if (action.equals("create"))
			{
				//scenario = new Scenario();
				//scenario.getPrjid();
				//scenario.setName(scenario.getProjectName());
				//	...
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
	public String getOutputVariables(Model model){
	
		return "outputvariables";
	}
	
	@RequestMapping(value="runmultiscenario",method=RequestMethod.GET)
	public String getProjectParameters(Model model){
	
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
}