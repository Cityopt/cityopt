package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class OptimizationSetDTO {

	@Getter @Setter private int optid;
//	@Getter @Setter private ObjectiveFunction objectivefunction;
//	@Getter @Setter private Scenario scenario;
	@Getter @Setter private Integer prjid;
	@Getter @Setter private Date createdon;
	@Getter @Setter private Date updatedon;
	@Getter @Setter private Integer createdby;
	@Getter @Setter private Integer updatedby;
//	@Getter @Setter private Set<OptSearchConst> optsearchconsts = new HashSet<OptSearchConst>(0);

}
