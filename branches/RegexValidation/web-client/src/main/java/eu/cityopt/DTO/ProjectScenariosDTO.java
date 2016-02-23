package eu.cityopt.DTO;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class ProjectScenariosDTO extends ProjectDTO {
		@Getter @Setter private Set<ScenarioDTO> scenarios;
}
