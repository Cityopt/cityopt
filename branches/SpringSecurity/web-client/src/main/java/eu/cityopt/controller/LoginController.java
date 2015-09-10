package eu.cityopt.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
	
		
	@RequestMapping(value="login", method=RequestMethod.GET)
	public String showLogin(){
		return "login";
	}
}
