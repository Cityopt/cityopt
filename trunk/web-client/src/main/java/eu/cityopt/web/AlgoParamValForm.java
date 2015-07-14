package eu.cityopt.web;

import java.util.Map;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public class AlgoParamValForm {
	@Getter @Setter private Map<Integer, String> valueByParamId = new HashMap<>();
}
