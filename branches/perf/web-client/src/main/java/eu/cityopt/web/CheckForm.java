package eu.cityopt.web;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class CheckForm {
	@Getter @Setter public Map<Integer, Check> checkById = new HashMap<Integer, Check>();
}
