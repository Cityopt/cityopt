package eu.cityopt.sim.eval.apros;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import eu.cityopt.sim.eval.SimulationOutput;

/**
 * An Apros simulation run.
 * These are created with AprosRunner.start. 
 * @author ttekth
 *
 */
public class AprosJob implements Future<SimulationOutput> {

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCancelled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDone() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SimulationOutput get() throws InterruptedException,
            ExecutionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimulationOutput get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

}
