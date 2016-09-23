package eu.cityopt.sim.eval.util;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * Helper methods for processing exceptions.
 *
 * @author Hannu Rummukainen
 */
public class ExceptionHelpers {
    /**
     * Peel off the common exception wrapper classes ExecutionException,
     * CompletionException and RuntimeException.
     * @return a non-null cause
     */
    public static Throwable peelCommonWrappers(Throwable throwable) {
        while (throwable.getCause() != null
                && (throwable instanceof ExecutionException
                        || throwable instanceof CompletionException
                        || throwable.getClass() == RuntimeException.class)) {
            throwable = throwable.getCause();
        }
        return throwable;
    }
}
