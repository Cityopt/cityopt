package eu.cityopt.sim.eval;

/**
 * Non-specific exception raised by the evaluation code.
 * 
 * @author Hannu Rummukainen
 */
@Deprecated
@SuppressWarnings("serial")
public class EvaluationException extends Exception {
    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
