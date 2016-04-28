package eu.cityopt.DTO;

import java.util.Set;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

public class UserGroupDTO extends BaseDTO {

	@Getter @Setter private int usergroupid;
	@Getter @Setter private String name;
//	@Getter @Setter private Set<UserGroupProject> usergroupprojects = new HashSet<UserGroupProject>(
//			0);
}
