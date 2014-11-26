package com.cityopt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cityopt.model.Project;
import com.cityopt.model.Scenario;
import com.cityopt.service.ProjectService;
import com.pluralsight.model.GoalReport;



@Controller
public class ProjectController {

	@Autowired
	ProjectService projectService; 

	//@Autowired
	//ScenarioService scenarioService; 

	@RequestMapping(value="getProjects",method=RequestMethod.GET)
	public String getGoalReports(Model model){
		List<Project> projects = projectService.findAllProjects();
		model.addAttribute("projects",projects);
		
		return "getProjects";
	}	

	@RequestMapping(value="createproject",method=RequestMethod.GET)
	public String getCreateProject(Model model){
	
		return "createproject";
	}

	@RequestMapping(value="createscenario",method=RequestMethod.GET)
	public String getCreateScenario(Model model){
	
		return "createscenario";
	}

	@RequestMapping(value="openproject", method=RequestMethod.GET)
	public String getStringProjects(Model model, @RequestParam(value="prjid", required=false) String prjid)
	{
		List<Project> projects = projectService.findAllProjects();
		model.addAttribute("projects",projects);
	
		if (prjid != null)
		{
			Project project = projectService.findByID(Integer.parseInt(prjid));
			return "editproject";
		}
		
		return "openproject";
	}	

	@RequestMapping(value="index",method=RequestMethod.GET)
	public String getIndex(Model model){
	
		return "index";
	}

	@RequestMapping(value="start",method=RequestMethod.GET)
	public String getStart(Model model){
	
		return "start";
	}

	@RequestMapping(value="deleteproject",method=RequestMethod.GET)
	public String getDeleteProject(Model model){
		List<Project> projects = projectService.findAllProjects();
		model.addAttribute("projects",projects);
	
		return "deleteproject";
	}

	@RequestMapping(value="editproject",method=RequestMethod.GET)
	public String getEditProject(Model model){
	
		return "editproject";
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
