package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class AppUserDTO {
	@Getter @Setter private int userid;
	@Getter @Setter private String name;
	@Getter @Setter private String password;
//	private Set<UserGroupProject> usergroupprojects = new HashSet<UserGroupProject>(
//			0);

}
