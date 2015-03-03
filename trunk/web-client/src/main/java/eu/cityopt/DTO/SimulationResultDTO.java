package eu.cityopt.DTO;

import java.util.Date;

import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.TimeSeries;
import lombok.Getter;
import lombok.Setter;

public class SimulationResultDTO {

	@Getter @Setter private int simresid;
	@Getter @Setter private OutputVariableDTO outputvariable;
	@Getter @Setter private ScenarioDTO scenario;
	@Getter @Setter private TimeSeriesDTO timeseries;
}
