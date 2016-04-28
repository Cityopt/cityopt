package eu.cityopt.web;

import java.util.HashMap;
import java.util.Map;

import eu.cityopt.DTO.ExtParamValSetDTO;
import lombok.Getter;
import lombok.Setter;

public class ExtParamValSetForm {
	@Getter @Setter private ExtParamValSetDTO extParamValSet;
	@Getter @Setter private Map<Integer, String> valueByParamId = new HashMap<>();
	@Getter @Setter private Map<Integer, String> commentByParamId = new HashMap<>();
}
