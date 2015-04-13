package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class OptSearchConstDTO {

	@Getter @Setter private Integer optsearchconstid;
//	@Getter @Setter private OptimizationSet optimizationset;
	@Getter @Setter private SearchConstraintDTO searchconstraint;
	
}
