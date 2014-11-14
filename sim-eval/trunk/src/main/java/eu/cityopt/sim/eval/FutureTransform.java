package eu.cityopt.sim.eval;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Transform the value of a Future after it becomes available.
 * 
 * @author Hannu Rummukainen
 * @param <T> input type of the transform, output of the required Future
 * @param <U> output type of the transform
 */
public abstract class FutureTransform<T, U> implements Future<U> {
    private Future<T> input;
    private boolean transformDone;
    private U transformResult;
    private Exception transformException;

    FutureTransform(Future<T> input) {
        this.input = input;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return input.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return input.isCancelled();
    }

    @Override
    public boolean isDone() {
        return input.isDone();
    }

    @Override
    public U get() throws InterruptedException, ExecutionException {
        if (!transformDone) {
            updateResult(input.get());
        }
        return getResultOrThrow();
    }

    @Override
    public U get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        if (!transformDone) {
            updateResult(input.get(timeout, unit));
        }
        return getResultOrThrow();
    }

    private void updateResult(T inputValue) {
        try {
            transformResult = transform(inputValue);
        } catch (Exception e) {
            transformException = e;
        }
        transformDone = true;
    }

    private U getResultOrThrow() throws ExecutionException {
        if (transformException != null) {
            throw new ExecutionException("Transform failed", transformException);
        }
        return transformResult;
    }

    protected abstract U transform(T input) throws Exception;
}
