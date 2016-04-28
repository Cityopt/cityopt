package eu.cityopt.sim.eval.apros;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.Job;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.files.IFile;
import org.simantics.simulation.scheduling.listening.StateListener;
import org.simantics.simulation.scheduling.status.JobFailed;
import org.simantics.simulation.scheduling.status.JobFinished;
import org.simantics.simulation.scheduling.status.JobStatus;
import org.simantics.simulation.scheduling.status.JobSucceeded;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;

import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;

/**
 * An Apros simulation run.
 * These are created with {@link AprosRunner#start}.
 * @author ttekth
 *
 */
public class AprosJob extends CompletableFuture<SimulationOutput>
        implements StateListener<JobStatus> {
    private final Executor executor;
    private final SimulationInput input;
    private final Instant runStart;
    final JobConfiguration conf;
    private Job job;
    private ByteArrayOutputStream ostr = new ByteArrayOutputStream();
    private volatile Instant runEnd;

    AprosJob(Executor executor, SimulationInput input,
            Experiment xpt, JobConfiguration conf, Instant runStart) {
        this.executor = executor;
        this.input = input;
        this.runStart = runStart;
        this.conf = conf;
        job = xpt.createJob("job", conf);
        StatusLoggingUtils.redirectJobLog(job, ostr);
        job.status().addListener(this);
    }

    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (isCancelled())
            return true;
        if (isDone() || !mayInterruptIfRunning)
            return false;
        if ( ! super.cancel(mayInterruptIfRunning))
            return false;
        Experiment x = job.getExperiment();
        job = null;
        x.dispose();
        ostr.reset();
        return true;
    }

    @Override
    public synchronized void stateChanged(JobStatus newState) {
        if (job != null && newState instanceof JobFinished && runEnd == null) {
            JobFinished st = (JobFinished) newState;
            runEnd = Instant.now();
            executor.execute(() -> completeJob(st));
        }
    }

    public synchronized void completeJob(JobFinished st) {
        try {
            if (isCancelled()) {
                throw new CancellationException();
            }
            SimulationOutput output;
            if (st instanceof JobSucceeded) {
                SimulationResults
                    res = new SimulationResults(input, ostr.toString());
                
                System.out.printf(res.getMessages());
                
                
                try {
                    System.out.printf("Result files: %s%n", String.join(", ",
                            st.outputDirectory.files().keySet()));
                    for (IFile f : st.outputDirectory.files().values()) {
                        try (InputStream str = f.open();
                             BufferedReader rd = AprosIO.makeReader(str)) {
                            AprosIO.readResultFile(rd, res);
                        }
                    }
                } catch (IOException e) {
                    throw new IOException(
                            "Result retrieval failed", e);
                }
                //TODO Check that all outputs were found.
                output = res;
            } else {
                String reason = (st instanceof JobFailed)
                        ? ((JobFailed) st).description : st.toString();
                String messages = reason + "\n" + ostr.toString();
                output = new SimulationFailure(input, false, reason, messages);
            }
            Experiment x = job.getExperiment();
            job = null;
            x.dispose();
            ostr.reset();
            output.runStart = runStart;
            output.runEnd = runEnd;
            complete(output);
        } catch (Throwable t) {
            completeExceptionally(t);
        }
    }
}
