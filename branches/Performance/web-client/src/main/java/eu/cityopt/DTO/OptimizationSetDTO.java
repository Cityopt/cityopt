package eu.cityopt.DTO;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.Scenario;
import lombok.Getter;
import lombok.Setter;

public class OptimizationSetDTO extends BaseDTO {

	@Getter @Setter private int optid;
	@Getter @Setter private ObjectiveFunctionDTO objectivefunction;
	@Getter @Setter private ExtParamValSetDTO extparamvalset;
	@Getter @Setter private Integer prjid;
//	@Getter @Setter private ScenarioDTO scenario;
	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private Date createdon;
	@Getter @Setter private Date updatedon;
	@Getter @Setter private Integer createdby;
	@Getter @Setter private Integer updatedby;
	@Getter @Setter private String name;
	@Getter @Setter private String description;
	@Getter @Setter private Date optstart;
}
