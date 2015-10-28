package eu.cityopt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// ToDo simple HTML -pages info about CityOpt.
// Keep it simple here.

@Controller
public class InfoController {

	@RequestMapping(value ="createproject_info", method=RequestMethod.GET)
	public String InfoCreateProject(){
		return "createproject_info";
	}
	
}
