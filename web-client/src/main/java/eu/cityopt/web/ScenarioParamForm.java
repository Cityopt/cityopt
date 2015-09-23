package eu.cityopt.web;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class ScenarioParamForm {
	@Getter @Setter private Map<Integer, String> valueByInputId = new HashMap<>();
	
}
