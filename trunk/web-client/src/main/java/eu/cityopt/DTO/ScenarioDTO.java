package eu.cityopt.DTO;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.model.ScenarioGenerator;

//@Data
//@DomainClass(domainClass = Scenario.class)
public class ScenarioDTO {
	//@MappedBy(readOnly = true)
	@Getter @Setter private int scenid;
//	@Getter @Setter private ProjectDTO project;
	//@Getter @Setter private int prjid;
	//@Getter @Setter private ScenarioGenerator scenariogenerator;
	@Getter @Setter private String name;
	@Getter @Setter private String description;
	@Getter @Setter private Date createdon;
	@Getter @Setter private Date updatedon;
	@Getter @Setter private Integer createdby;
	@Getter @Setter private Integer updatedby;
//	private Set<ScenarioMetrics> scenariometricses = new HashSet<ScenarioMetrics>(
//			0);
//	private Set<InputParamVal> inputparamvals = new HashSet<InputParamVal>(0);
//	private Set<OptimizationSet> optimizationsets = new HashSet<OptimizationSet>(
//			0);
//	private Set<SimulationResult> simulationresults = new HashSet<SimulationResult>(
//			0);

}
