package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class UserGroupProjectDTO {

	@Getter @Setter private Integer usergroupprojectid;
	@Getter @Setter private UserGroupDTO usergroup;
	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private AppUserDTO appuser;

}
