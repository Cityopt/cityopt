package eu.cityopt.DTO;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class AppUserDTO  extends BaseDTO{
	
	@Getter @Setter private int userid;
	
	@Size(min=5,message="At least 5 characters")
	@Getter @Setter private String name;
	
	@Size(min=5,message="At least 5 characters")
	@Getter @Setter private String password;
	
	@Getter @Setter private Boolean enabled;
	
//	private Set<UserGroupProject> usergroupprojects = new HashSet<UserGroupProject>(
//		0);
}
