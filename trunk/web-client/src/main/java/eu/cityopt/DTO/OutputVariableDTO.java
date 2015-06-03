package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public class OutputVariableDTO extends BaseDTO {

	@Getter @Setter private int outvarid;
	@Getter @Setter private ComponentDTO component;
	@Getter @Setter private String name;
	@Getter @Setter private Boolean selected;
	@Getter @Setter private UnitDTO unit;
	@Getter @Setter private TypeDTO type;
//	@Getter @Setter private Set<SimulationResult> simulationresults = new HashSet<SimulationResult>(
//			0);

}
