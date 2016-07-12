package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * The value of an expression is invalid or not available.
 * 
 * @author Hannu Rummukainen
 */
@SuppressWarnings("serial")
public class InvalidValueException extends ScriptException {
    InvalidValueException(String message) {
        super(message);
    }

    InvalidValueException(Type expectedType, Object value) {
        super("Expected " + expectedType.name + " but got " + value);
    }
}
