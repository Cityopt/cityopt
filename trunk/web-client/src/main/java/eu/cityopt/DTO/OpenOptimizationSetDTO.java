package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.model.ExtParamValSet;

public class OpenOptimizationSetDTO  extends BaseDTO{

	@Getter @Setter private int id;
	@Getter @Setter private OptimizationSetType optSetType;
	@Getter @Setter private String name;
	@Getter @Setter private String description;
	@Getter @Setter private Date optstart;
	
	public boolean isDatabaseSearch() {
		return optSetType == OptimizationSetType.DatabaseSearch;
	}

}
