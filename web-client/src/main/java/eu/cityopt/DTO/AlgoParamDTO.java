package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;


public class AlgoParamDTO extends BaseDTO{

	@Getter @Setter private int aparamsid;
	@Getter @Setter private AlgorithmDTO algorithm;
	@Getter @Setter private String name;
	@Getter @Setter private String defaultvalue;
//	private Set<AlgoParamVal> algoparamvals = new HashSet<AlgoParamVal>(0);

}
