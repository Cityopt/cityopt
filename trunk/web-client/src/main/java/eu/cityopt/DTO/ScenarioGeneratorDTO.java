package eu.cityopt.DTO;

import java.util.HashSet;
import java.util.Set;

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

public class ScenarioGeneratorDTO {

	@Getter @Setter private int scengenid;
	@Getter @Setter private AlgorithmDTO algorithm;
//	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private ExtParamValSetDTO extparamvalset;
	@Getter @Setter private String status;
	@Getter @Setter private String log;
	@Getter @Setter private String name;
	
	@Getter @Setter private Set<DecisionVariableDTO> decisionvariables;
	@Getter @Setter private Set<ModelParameterDTO> modelparameters;
//	@Getter @Setter private Set<ScenGenResultDTO> scengenresults = new HashSet<ScenGenResult>(0);
	@Getter @Setter private Set<ScenGenOptConstraintDTO> scengenoptconstraints;
//	@Getter @Setter private Set<ScenarioDTO> scenarios;
	@Getter @Setter private Set<AlgoParamValDTO> algoparamvals;
	@Getter @Setter private Set<ScenGenObjectiveFunctionDTO> scengenobjectivefunctions;
}

