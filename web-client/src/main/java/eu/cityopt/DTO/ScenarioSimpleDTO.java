package eu.cityopt.DTO;

import java.util.Date;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.model.ScenarioGenerator;


public class ScenarioSimpleDTO extends BaseDTO {
	@Getter @Setter private int scenid;
	@Getter @Setter private String name;	
}
