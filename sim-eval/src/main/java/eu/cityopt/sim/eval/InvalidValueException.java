package eu.cityopt.sim.eval;

import javax.script.ScriptException;

/**
 * The value of an expression is invalid or not available.
 * 
 * @author Hannu Rummukainen
 */
@SuppressWarnings("serial")
public class InvalidValueException extends ScriptException {
    InvalidValueException(Type expectedType, Object value) {
        super("Expected " + expectedType + " but got " + value);
    }

    InvalidValueException(Type expectedType, Object value, String source) {
        super("Expected " + expectedType + " but got " + value + " from script: " + source);
    }

    InvalidValueException(Object value, String source) {
        super("Invalid value " + value + " from script: " + source);
    }
}
