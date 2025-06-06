package eu.cityopt.web;

import lombok.Getter;
import lombok.Setter;

public class ParamForm {
	@Getter @Setter private String name;
	@Getter @Setter private String value;
	@Getter @Setter private String min;
	@Getter @Setter private String max;
	@Getter @Setter private String unit;
	@Getter @Setter private String comment;
}
