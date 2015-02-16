package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ComponentInputParamDTO {
	@Getter @Setter private int componentid;
	@Getter @Setter private String componentname;
	@Getter @Setter private int inputid;
	@Getter @Setter private  String inputparametername;
	@Getter @Setter private  Integer scendefinitionid; //nullable
	@Getter @Setter private  String value;
	@Getter @Setter private Integer scenarioid; //nullable
	@Getter @Setter private  int prjid;
}
