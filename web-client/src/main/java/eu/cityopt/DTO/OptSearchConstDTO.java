package eu.cityopt.DTO;

import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptimizationSet;
import lombok.Getter;
import lombok.Setter;

public class OptSearchConstDTO {

	@Getter @Setter private Integer optsearchconstid;
	@Getter @Setter private OptimizationSetDTO optimizationset;
	@Getter @Setter private OptConstraintDTO optconstraint;	
}
