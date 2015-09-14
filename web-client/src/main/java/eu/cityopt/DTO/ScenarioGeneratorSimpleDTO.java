package eu.cityopt.DTO;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Size;

import eu.cityopt.model.AlgoParamVal;
import eu.cityopt.model.Algorithm;
import eu.cityopt.model.DecisionVariable;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ModelParameter;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenGenObjectiveFunction;
import eu.cityopt.model.ScenGenOptConstraint;
import eu.cityopt.model.ScenGenResult;
import eu.cityopt.model.Scenario;
import lombok.Getter;
import lombok.Setter;

public class ScenarioGeneratorSimpleDTO extends BaseDTO {

	@Getter @Setter private int scengenid;	
}

