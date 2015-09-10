package eu.cityopt.DTO;

import lombok.Getter;
import lombok.Setter;

public abstract class BaseDTO {
	/**
	 * This field maps the db version column, do not overwrite as this might lead to concurrency issues
	 */
	@Getter @Setter protected Integer version;
}
