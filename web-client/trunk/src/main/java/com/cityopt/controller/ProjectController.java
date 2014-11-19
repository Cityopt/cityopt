package com.cityopt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cityopt.model.Project;
import com.cityopt.service.ProjectService;
import com.pluralsight.model.GoalReport;



@Controller
public class ProjectController {

	@Autowired
	ProjectService projectService; 
		
	@RequestMapping(value="getProjects",method=RequestMethod.GET)
	public String getGoalReports(Model model){
		List<Project> projects = projectService.findAllProjects();
		model.addAttribute("projects",projects);
		
		return "getProjects";
	}	
	
}
