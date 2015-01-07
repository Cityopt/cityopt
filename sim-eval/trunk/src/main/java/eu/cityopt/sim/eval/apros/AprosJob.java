package eu.cityopt.sim.eval.apros;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.Job;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.status.JobFinished;
import org.simantics.simulation.scheduling.status.JobRunning;
import org.simantics.simulation.scheduling.status.JobSucceeded;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;
import org.simantics.simulation.scheduling.status.StatusWaitingUtils;

import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;

/**
 * An Apros simulation run.
 * These are created with AprosRunner.start. 
 * @author ttekth
 *
 */
public class AprosJob implements Future<SimulationOutput> {
    private final AprosRunner runner;
    private final SimulationInput input;
    private Job job;
    private boolean cancelled = false;
    private SimulationOutput output = null;
    private ByteArrayOutputStream ostr = new ByteArrayOutputStream();
    
    AprosJob(AprosRunner runner, SimulationInput input,
             Experiment xpt, JobConfiguration conf) {
        this.runner = runner;
        this.input = input;
        job = xpt.createJob("job", conf);
        StatusLoggingUtils.redirectJobLog(job, ostr);
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (cancelled)
            return true;
        if (isDone() || !mayInterruptIfRunning)
            return false;
        Experiment x = job.getExperiment();
        job = null;
        cancelled = true;
        x.dispose();
        ostr.reset();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return job == null || !(job.status().get() instanceof JobRunning);
    }

    @Override
    public synchronized SimulationOutput get() throws InterruptedException,
            ExecutionException {
        JobFinished st;
        if (cancelled)
            throw new CancellationException();
        if (job != null) {
            st = StatusWaitingUtils.waitFor(job);
            if (st instanceof JobSucceeded) {
                SimulationResults
                    res = new SimulationResults(input, ostr.toString());
                output = res;
                //TODO Retrieve the output and store in res.
            } else {
                output = new SimulationFailure(
                        input, st.toString() + "\n" + ostr.toString());
            }
            Experiment x = job.getExperiment();
            job = null;
            x.dispose();
            ostr.reset();
        }
        return output;
    }

    @Override
    public SimulationOutput get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        //TODO Add this once implemented in the library.
        throw new UnsupportedOperationException("Timed wait not supported.");
    }

}
