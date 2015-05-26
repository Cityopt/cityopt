package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class InputParamValDTO {

	@Getter @Setter private Integer scendefinitionid;
	@Getter @Setter private InputParameterDTO inputparameter;
	@Getter @Setter private ScenarioDTO scenario;
	@Getter @Setter private String value;
	@Getter @Setter private Date createdon;
	@Getter @Setter private Date updatedon;
	@Getter @Setter private Integer createdby;
	@Getter @Setter private Integer updatedby;
	@Getter @Setter private Integer datarelid;

}
