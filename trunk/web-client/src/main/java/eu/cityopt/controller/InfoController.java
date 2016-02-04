package eu.cityopt.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.service.EntityNotFoundException;

// ToDo simple HTML -pages info about CityOpt.
// Keep it simple here.

@Controller
public class InfoController {
		
	@RequestMapping(value ="createproject_info", method=RequestMethod.GET)
	public String InfoCreateProject(){		
		return "createproject_info";				
	}
		
	// Pagination test
	@RequestMapping(value ="pagination_prototype", method=RequestMethod.GET)
	public String PaginationPrototype(){
		return "pagination_prototype";
	}
	
	@RequestMapping( value="importdata_info", method=RequestMethod.GET)
	public String ImportDataInfo(){		
		return "importdata_info";
	}

	@RequestMapping( value="editproject_info", method=RequestMethod.GET)
	public String EditProjectInfo(){		
		return "editproject_info";
	}

	@RequestMapping( value="editscenario_info", method=RequestMethod.GET)
	public String EditScenarioInfo(){		
		return "editscenario_info";
	}

	@RequestMapping( value="projectparameters_info", method=RequestMethod.GET)
	public String ProjectParametersInfo(){		
		return "projectparameters_info";
	}

}
