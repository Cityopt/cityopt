package eu.cityopt.DTO;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class AlgorithmDTO {

	@Getter @Setter private int algorithmid;
	@Getter @Setter private String description;
//	private Set<AlgoParam> algoparams = new HashSet<AlgoParam>(0);
//	private Set<ScenarioGenerator> scenariogenerators = new HashSet<ScenarioGenerator>(
//			0);
}
