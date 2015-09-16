package eu.cityopt.DTO;

import java.util.Date;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.model.ScenarioGenerator;


public class ScenarioDTO extends BaseDTO {
	@Getter @Setter private int scenid;
	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private ScenarioGeneratorSimpleDTO scenariogenerator;
	@Size(min=1,max=50)
	@Getter @Setter private String name;
	@Getter @Setter private String description;
	@Getter @Setter private Date createdon;
	@Getter @Setter private Date updatedon;
	@Getter @Setter private Integer createdby;
	@Getter @Setter private Integer updatedby;
	@Getter @Setter private Date runend;
	@Getter @Setter private Date runstart;
	@Getter @Setter private String status;
	@Getter @Setter private String log;
//	private Set<ScenarioMetrics> scenariometricses = new HashSet<ScenarioMetrics>(
//			0);
//	private Set<InputParamVal> inputparamvals = new HashSet<InputParamVal>(0);
//	private Set<OptimizationSet> optimizationsets = new HashSet<OptimizationSet>(
//			0);
//	private Set<SimulationResult> simulationresults = new HashSet<SimulationResult>(
//			0);

}
