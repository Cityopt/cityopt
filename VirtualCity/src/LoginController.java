//package com.studytrails.tutorials.springsecuritycustomloginpage;
	 
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
	 
@Controller
public class LoginController {
	 
	@RequestMapping(value="/main", method = RequestMethod.GET)
	public String printWelcome(ModelMap model, Principal principal ) {
		String name = principal.getName();
		model.addAttribute("username", name);
		return "main_page";
	}
	 
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		return "login_page";
	}
	 
	@RequestMapping(value="/loginError", method = RequestMethod.GET)
	public String loginError(ModelMap model) {
		model.addAttribute("error", "true");
		return "login_page";
	}
}
