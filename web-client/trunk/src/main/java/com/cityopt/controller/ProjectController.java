package com.cityopt.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cityopt.model.Project;
import com.cityopt.service.ProjectService;


@Controller
@SessionAttributes("project")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;

	@RequestMapping(value = "addProject", method = RequestMethod.GET)
	public String addProject(Model model, HttpSession session) {
		//Goal goal = new Goal();
		
		Project project = (Project)session.getAttribute("project");
		
		if (project == null)
		{
			project = new Project();
			//project.setMinutes(10);
		}		
		
		model.addAttribute("project", project);
				
		return "addProject";
	}
	
	@RequestMapping(value = "addProject", method = RequestMethod.POST)
	public String updateProject(@Valid @ModelAttribute("goal") Project project, BindingResult result) {
		
		System.out.println("result has errors: " + result.hasErrors());
		
		//System.out.println("Project set: " + project.getMinutes());
		
		if(result.hasErrors()) {
			return "addProject";
		}
		else
		{
			projectService.save(project);
		}
		
		return "redirect:index.jsp";
	}
	
	@RequestMapping(value="getProjects",method=RequestMethod.GET)
	public String getProjects(Model model){
		List<Project>projects = projectService.findAllProjects();		
		model.addAttribute("projects",projects);
		
		return "getProjects";		
	}
	
	/*@RequestMapping(value="getProjectReports",method=RequestMethod.GET)
	public String getProjectReports(Model model){
		List<ProjectReport> goalReports = projectService.findAllProjectReports();
		model.addAttribute("goalReports",goalReports);
		
		return "getProjectReports";
	}*/	
	
}
