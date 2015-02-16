package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class SimulationResultDTO {

	@Getter @Setter private int scenresid;
//	@Getter @Setter private OutputVariableDTO outputvariable;
//	@Getter @Setter private ScenarioDTO scenario;
	@Getter @Setter private Date time;
	@Getter @Setter private String value;

}
