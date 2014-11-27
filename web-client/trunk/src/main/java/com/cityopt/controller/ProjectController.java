package com.cityopt.controller;

import java.awt.Desktop.Action;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cityopt.model.Project;
import com.cityopt.service.ProjectService;



@Controller
public class ProjectController {

	ProjectForm projectForm;
	ProjectForm newProjectForm;
	Project project;
	
	@Autowired
	ProjectService projectService; 

	//@Autowired
	//ScenarioService scenarioService; 

	@RequestMapping(value="getProjects",method=RequestMethod.GET)
	public String getGoalReports(Model model) {
		List<Project> projects = projectService.findAllProjects();
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
	public String getCreateScenario(Model model) {
	
		return "createscenario";
	}

	@RequestMapping(value="openproject", method=RequestMethod.GET)
	public String getStringProjects(Map<String, Object> model, @RequestParam(value="prjid", required=false) String prjid)
	{
		List<Project> projects = projectService.findAllProjects();
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
			projectService.deleteProject(tempProject);
		}

		List<Project> projects = projectService.findAllProjects();
		model.addAttribute("projects",projects);

		return "deleteproject";
	}

	@RequestMapping(value="openscenario",method=RequestMethod.GET)
	public String getOpenScenario (Model model){
		//List<Scenario> scenarios = scenarioService.findAllScenarios();
		//model.addAttribute("scenarios",scenarios);
	
		return "openscenario";
	}

	@RequestMapping(value="editscenario",method=RequestMethod.GET)
	public String getEditScenario (Model model){
		//List<Scenario> scenarios = scenarioService.findAllScenarios();
		//model.addAttribute("scenarios",scenarios);
	
		return "editscenario";
	}

	@RequestMapping(value="deletescenario",method=RequestMethod.GET)
	public String getDeleteScenario (Model model){
		//List<Scenario> scenarios = scenarioService.findAllScenarios();
		//model.addAttribute("scenarios",scenarios);
	
		return "deletescenario";
	}

	
	@RequestMapping(value="usermanagement",method=RequestMethod.GET)
	public String getUserManagement(Model model){
	
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
