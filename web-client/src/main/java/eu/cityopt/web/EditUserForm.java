package eu.cityopt.web;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import eu.cityopt.DTO.ExtParamValSetDTO;

public class EditUserForm {

	@Getter @Setter private Map<Integer, String> names;
	@Getter @Setter private Map<Integer, String> passwords;
	@Getter @Setter private Map<Integer, Boolean> enables;		
}
