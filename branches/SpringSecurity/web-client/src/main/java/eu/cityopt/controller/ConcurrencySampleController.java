package eu.cityopt.controller;

import java.util.Map;

import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ProjectService;

@Controller
@RequestMapping(value="ConcurrencySample")
public class ConcurrencySampleController {
	@Autowired
	ProjectService projectService; 
	
	@RequestMapping(value="/editproject", method=RequestMethod.GET)
	public String getEditProjectCon(Map<String, Object> model, @RequestParam(value="prjid", required=false) String prjid) {
//		model.put("errorMessage", "Test");
		if (prjid != null)
		{
			ProjectDTO project = null;
			try {
				project = projectService.findByID(Integer.parseInt(prjid));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.put("projectForm", project);
		}
		else if (!model.containsKey("projectForm"))
		{
			ProjectDTO newProject = new ProjectDTO();
			model.put("projectForm", newProject);
			return "editprojectcon";
		}

		return "editprojectcon";
	}
	
	@RequestMapping(value="/editproject", method=RequestMethod.POST)
	public String getEditProjectConPost(@ModelAttribute("projectForm") ProjectDTO project, 
		@RequestParam(value="action", required=false) String action, Map<String, Object> model) {
	
		if (project != null && action != null)
		{
			if (action.equals("create"))
			{
			}
			else if (action.equals("update"))
			{
			}
			
			try{
			project = projectService.save(project,0,0);
			}catch(ObjectOptimisticLockingFailureException e){
				model.put("errorMessage", "This project has been updated in the meantime, please reload.");
			}
			
//			model.put("projectForm", project);
		}
		return "editprojectcon";
	}
}
