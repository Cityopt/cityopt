package eu.cityopt.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.service.UserGroupProjectService;

@Service("customUserDetailService")
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	UserGroupProjectService userGroupProjectService;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		UserDetails matchingUser=	userGroupProjectService.findUserDetails(username);
		
		if(matchingUser == null){
			throw new UsernameNotFoundException("Wrong username or password");
		}
		
		return matchingUser;
	}

}
