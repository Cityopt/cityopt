package eu.cityopt.sim.eval.apros;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.Job;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.files.IFile;
import org.simantics.simulation.scheduling.status.JobFinished;
import org.simantics.simulation.scheduling.status.JobRunning;
import org.simantics.simulation.scheduling.status.JobSucceeded;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;
import org.simantics.simulation.scheduling.status.StatusWaitingUtils;

import com.google.common.primitives.Doubles;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.Type;

/**
 * An Apros simulation run.
 * These are created with AprosRunner.start. 
 * @author ttekth
 *
 */
public class AprosJob implements Future<SimulationOutput> {
    private final AprosRunner runner;
    private final SimulationInput input;
    final JobConfiguration conf;
    private Job job;
    private boolean cancelled = false;
    private SimulationOutput output = null;
    private ByteArrayOutputStream ostr = new ByteArrayOutputStream();
    
    AprosJob(AprosRunner runner, SimulationInput input,
             Experiment xpt, JobConfiguration conf) {
        this.runner = runner;
        this.input = input;
        this.conf = conf;
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
                try {
                    for (IFile f : st.outputDirectory.files().values()) {
                        readResultFile(f, res);        
                    }
                } catch (IOException e) {
                    throw new ExecutionException(
                            "Result retrieval failed", e);
                }
                //TODO Check that all outputs were found.
                output = res;
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

    private static String[] readAndSplit(BufferedReader rd)
            throws IOException {
        String line = rd.readLine(); 
        return line == null ? null : line.trim().split("[ \t]+");
    }
        
    
    private void readResultFile(IFile file, SimulationResults res) 
            throws IOException {
        Namespace ns = res.getNamespace();
        try (InputStream str = file.open();
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(str))) {
            String [] line = readAndSplit(in);
            int n_cols;
            if (line.length != 1
                || (n_cols = Integer.parseInt(line[0])) < 1) {
                throw new IOException("Bad first line");
            }
            List<List<Double>> vals = new ArrayList<>(n_cols);
            // First column is always time.
            readAndSplit(in);
            vals.add(new ArrayList<>());
            class Output {
                String comp, name;
                int column;
                Output(String[] line, int col) {
                    comp = line[0];
                    name = line[1];
                    column = col;
                }
            }
            List<Output> outs = new ArrayList<>();
            for (int i = 1; i != n_cols; ++i) {
                line = readAndSplit(in);
                if (line == null) {
                    throw new IOException("Premature EOF");
                }
                if (line.length != 2) {
                    throw new IOException(
                            "Bad header line " + (i + 1) + ": " + line.length
                            + " columns");
                }
                Namespace.Component comp = ns.components.get(line[0]);
                if (comp != null && comp.outputs.containsKey(line[1])) {
                    outs.add(new Output(line, i));
                    vals.add(new ArrayList<>());
                }
            } 
            for (int ln = n_cols + 2;
                 (line = readAndSplit(in)) != null;
                 ++ln) {
                if (line.length < n_cols) {
                    throw new IOException(
                            "Line " + ln + " too short: " + line.length
                            + " < " + n_cols + " columns");
                }
                vals.get(0).add(Double.parseDouble(line[0]));
                int i = 1;
                for (Output out : outs) {
                    vals.get(i++).add(Double.parseDouble(line[out.column]));
                }
            }
            int i = 1;
            for (Output out : outs) {
                res.put(out.comp, out.name,
                        ns.evaluator.makeTS(Type.TIMESERIES_LINEAR,
                                            Doubles.toArray(vals.get(0)),
                                            Doubles.toArray(vals.get(i++))));
            }
        }
    }

    @Override
    public SimulationOutput get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        //TODO Add this once implemented in the library.
        throw new UnsupportedOperationException("Timed wait not supported.");
    }

}
