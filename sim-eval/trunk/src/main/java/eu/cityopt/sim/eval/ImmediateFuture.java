package eu.cityopt.sim.eval;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Wraps pre-computed data in a Future.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 * 
 * @param <T> contained data type
 */
public class ImmediateFuture<T> implements Future<T> {
    private T result;

    ImmediateFuture(T result) {
        this.result = result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return result;
    }
}
