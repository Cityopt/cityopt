package eu.cityopt.DTO;

import java.util.Date;

import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.ScenGenResult;
import lombok.Getter;
import lombok.Setter;

public class ObjectiveFunctionResultDTO  extends BaseDTO{

	@Getter @Setter private int obtfunctionid;
	@Getter @Setter private int scengenid;
	@Getter @Setter private int objectivefunctionresultid;	
	@Getter @Setter private String value;
	
	
	@Getter @Setter private boolean scengenresultParetooptimal;	
	@Getter @Setter private boolean scengenresultFeasible;
	
	@Getter @Setter private int scenID;
}
