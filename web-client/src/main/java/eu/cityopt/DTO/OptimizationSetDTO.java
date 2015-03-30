package eu.cityopt.DTO;

import java.util.Date;

import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.Scenario;
import lombok.Getter;
import lombok.Setter;

public class OptimizationSetDTO {

	@Getter @Setter private int optid;
	@Getter @Setter private ExtParamValSet extparamvalset;
//	@Getter @Setter private ObjectiveFunction objectivefunction;
//	@Getter @Setter private Scenario scenario;
	@Getter @Setter private Integer prjid;
	@Getter @Setter private Date createdon;
	@Getter @Setter private Date updatedon;
	@Getter @Setter private Integer createdby;
	@Getter @Setter private Integer updatedby;
	@Getter @Setter private String name;
	@Getter @Setter private Date optstart;
//	@Getter @Setter private Set<OptSearchConst> optsearchconsts = new HashSet<OptSearchConst>(0);
	
}
