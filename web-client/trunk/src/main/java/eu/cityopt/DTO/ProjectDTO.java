package eu.cityopt.DTO;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.model.Component;

//@Data
//@DomainClass("eu.cityopt.model.Project")
public class ProjectDTO {
		//@MappedBy(readOnly = true)
		@Getter @Setter private int Prjid;
		@Getter @Setter private String name;
		@Getter @Setter private String location;
		//@NotMapped
		@Getter @Setter private String projectCreator;
		
		@Getter @Setter private String description;
		
		@Getter @Setter private SimulationModelDTO simulationmodel;
		@Getter @Setter private String designtarget;
//		private Date timehorizon;
//		private Date createdon;
//		private Date updatedon;
//		private Integer createdby;
//		private Integer updatedby;
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
