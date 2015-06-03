package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ComponentInputParamDTO  extends BaseDTO{
	@Getter @Setter private int componentid;
	@Getter @Setter private String componentname;
	@Getter @Setter private int inputid;
	@Getter @Setter private  String inputparametername;
	@Getter @Setter private  Integer inputparamvalid; //nullable
	@Getter @Setter private  String value;
	@Getter @Setter private Integer scenarioid; //nullable
	@Getter @Setter private  int prjid;
}
