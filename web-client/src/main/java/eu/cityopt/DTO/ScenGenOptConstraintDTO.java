package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class ScenGenOptConstraintDTO extends BaseDTO {

	@Getter @Setter private int sgoptconstraintid;
	@Getter @Setter private OptConstraintDTO optconstraint;
	@Getter @Setter private ScenarioGeneratorDTO scenariogenerator;

}
