package eu.cityopt.sim.eval;

/**
 * The value of an expression is invalid or not available.
 * @author Hannu Rummukainen
 */
@SuppressWarnings("serial")
public class InvalidValueException extends Exception {
	InvalidValueException(Object value, String source) {
		super("Invalid value " + value + " from script: " + source);
	}
}
