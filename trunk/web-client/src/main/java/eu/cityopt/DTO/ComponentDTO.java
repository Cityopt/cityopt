package eu.cityopt.DTO;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class ComponentDTO {
	@Getter @Setter private int componentid;
	@Getter @Setter private ProjectDTO project;
	@Getter @Setter private String name;
	@Getter @Setter private String alias;
	@Getter @Setter private Serializable geometryblob;
//	@Getter @Setter private Set<InputParameter> inputparameters = new HashSet<InputParameter>(0);
	@Getter @Setter private Set<OutputVariableDTO> outputvariables = new HashSet<OutputVariableDTO>(0);

}
