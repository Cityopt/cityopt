package eu.cityopt.sim.eval;

/**
 * Superclass for exceptions raised by the evaluation code.
 * 
 * @author Hannu Rummukainen
 */
@SuppressWarnings("serial")
public class EvaluationException extends Exception {
    EvaluationException(String message) {
        super(message);
    }

    EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
