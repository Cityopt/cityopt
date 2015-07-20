package eu.cityopt.service.impl;

import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority{
	private static final long serialVersionUID = 1029928088340565343L;

	private String rolename;
	private String project;
	
	public GrantedAuthorityImpl(String rolename){
		this.rolename = rolename;
	}
	
	public GrantedAuthorityImpl(String rolename,String project){
		this.rolename = rolename;
		this.project = project;
	}
	
	public String getAuthority() {
		return this.rolename;
	}

}
