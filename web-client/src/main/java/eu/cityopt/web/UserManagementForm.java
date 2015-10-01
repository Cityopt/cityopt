package eu.cityopt.web;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class UserManagementForm {
	
	@Getter @Setter private Map<Integer, String> user = new HashMap<>();	
	@Getter @Setter private Map<Integer, String> password = new HashMap<>();
	@Getter @Setter private Map<Integer, Integer> userRole = new HashMap<>();
	@Getter @Setter private Map<Integer, String> project = new HashMap<>();
	@Getter @Setter private Map<Integer, Boolean> enabled = new HashMap<>();
	
	public int size() {		
		return user.size();
	}	
		
}

