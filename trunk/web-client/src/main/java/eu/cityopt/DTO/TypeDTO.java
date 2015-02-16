package eu.cityopt.DTO;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class TypeDTO {

	@Getter @Setter private int typeid;
	@Getter @Setter private String name;
//	@Getter @Setter private Set<Unit> units = new HashSet<Unit>(0);
//	@Getter @Setter private Set<ObjectiveFunction> objectivefunctions = new HashSet<ObjectiveFunction>(
//			0);
//	@Getter @Setter private Set<TimeSeries> timeserieses = new HashSet<TimeSeries>(0);
//	@Getter @Setter private Set<DecisionVariable> decisionvariables = new HashSet<DecisionVariable>(
//			0);
}
