package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * UserGroupProject assigns an user to a project using a group. A group/role must be specified by groupid.
 * A user can only be assigned to a project once. Therefore he can have only one group at a project
 * 
 * @author Michael
 *
 */
public class UserGroupProjectDTO extends BaseDTO {

	@Getter @Setter private Integer usergroupprojectid;
	@Getter @Setter private UserGroupDTO usergroup;
	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private AppUserDTO appuser;
	
	

}


