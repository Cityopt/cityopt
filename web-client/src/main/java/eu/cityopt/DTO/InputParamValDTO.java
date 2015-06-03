package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class InputParamValDTO  extends BaseDTO{

	@Getter @Setter private Integer inputparamvalid;
	@Getter @Setter private DataReliabilityDTO datareliability;
	@Getter @Setter private InputParameterDTO inputparameter;
	@Getter @Setter private ScenarioDTO scenario;
	@Getter @Setter private String value;
	@Getter @Setter private Date createdon;
	@Getter @Setter private Date updatedon;
	@Getter @Setter private Integer createdby;
	@Getter @Setter private Integer updatedby;

}
