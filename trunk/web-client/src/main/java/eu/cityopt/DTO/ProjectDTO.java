package eu.cityopt.DTO;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import eu.cityopt.model.ExtParamValSet;
import lombok.Getter;
import lombok.Setter;

public class ProjectDTO extends BaseDTO{

		@Getter @Setter private int prjid;
//		@Getter @Setter private ExtParamValSetDTO defaultextparamvalset;
	    @Size(min=1,max=50)
		@Getter @Setter private String name;
	    @NotEmpty
	    @Getter @Setter private String location;
		@Getter @Setter private String projectCreator;
		@Size(min=5,message="At least 5 characters")
		@Getter @Setter private String description;
//		@Getter @Setter private SimulationModelDTO simulationmodel;
		@NotEmpty
		@Getter @Setter private String designtarget;
		@Getter @Setter private Date timehorizon;
		@Getter @Setter private Date createdon;
		@Getter @Setter private Date updatedon;
		@Getter @Setter private Integer createdby;
		@Getter @Setter private Integer updatedby;
//		private Set<ObjectiveFunction> objectivefunctions = new HashSet<ObjectiveFunction>(
//				0);
		//@MappedBy(typeConverter = "scenarioToList")
		//@Getter @Setter private Set<ScenarioDTO> scenarios;
//		private Set<ScenarioGenerator> scenariogenerators = new HashSet<ScenarioGenerator>(
//				0);
//		@Getter @Setter private Set<Component> components;
//		private Set<OptConstraint> optconstraints = new HashSet<OptConstraint>(0);
//		private Set<SearchConstraint> searchconstraints = new HashSet<SearchConstraint>(
//				0);
//		private Set<Metric> metrics = new HashSet<Metric>(0);
//		private Set<UserGroupProject> usergroupprojects = new HashSet<UserGroupProject>(
//				0);
//		private Set<ExtParam> extparams = new HashSet<ExtParam>(0);

}
