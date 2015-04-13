package eu.cityopt.sim.eval.apros;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.simantics.simulation.scheduling.Experiment;
import org.simantics.simulation.scheduling.Job;
import org.simantics.simulation.scheduling.JobConfiguration;
import org.simantics.simulation.scheduling.files.IFile;
import org.simantics.simulation.scheduling.listening.StateListener;
import org.simantics.simulation.scheduling.status.JobFinished;
import org.simantics.simulation.scheduling.status.JobStatus;
import org.simantics.simulation.scheduling.status.JobSucceeded;
import org.simantics.simulation.scheduling.status.StatusLoggingUtils;

import com.google.common.primitives.Doubles;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.Type;

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
                try {
                    System.out.printf("Result files: %s%n", String.join(", ",
                            st.outputDirectory.files().keySet()));
                    for (IFile f : st.outputDirectory.files().values()) {
                        readResultFile(f, res);        
                    }
                } catch (IOException e) {
                    throw new IOException(
                            "Result retrieval failed", e);
                }
                //TODO Check that all outputs were found.
                output = res;
            } else {
                output = new SimulationFailure(
                        input, false, st.toString() + "\n" + ostr.toString());
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

    private static String[] readAndSplit(BufferedReader rd)
            throws IOException {
        String line = rd.readLine(); 
        return line == null ? null : line.trim().split("[ \t]+");
    }

    static List<String[]> parseResultHeader(BufferedReader in)
            throws IOException {
        String [] line = readAndSplit(in);
        int n_cols = 0;
        if (line != null && line.length == 1) {
            try {
                n_cols = Integer.parseInt(line[0]);
            } catch (NumberFormatException e) {}
        }
        if (n_cols < 1) {
            throw new IOException("Bad first line");
        }

        // First column is always time.
        line = readAndSplit(in);
        if (line == null) {
            throw new IOException("Premature EOF");
        }
        if (line.length != 2
                || ! line[0].equals("SIMULATION")
                || ! line[1].equals("TIME")) {
            throw new IOException("Expected SIMULATION TIME, got "
                + String.join(" ",  line));
        }

        List<String[]> variables = new ArrayList<>();
        for (int i = 1; i != n_cols; ++i) {
            line = readAndSplit(in);
            if (line == null) {
                throw new IOException("Premature EOF");
            }
            if (line.length != 2) {
                throw new IOException(
                        "Bad header line " + (i + 1) + ": "
                        + line.length + " columns");
            }
            variables.add(line);
        }
        return variables;
    }

    private static void readResultFile(IFile file, SimulationResults res) 
            throws IOException {
        Namespace ns = res.getNamespace();
        try (InputStream str = file.open();
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(str))) {
            List<String []> variables = parseResultHeader(in);
            int n_cols = variables.size() + 1;
            List<String []> names = new ArrayList<>();
            List<Integer> cols = new ArrayList<>();
            List<Type> types = new ArrayList<>();
            List<List<Double>> vals = new ArrayList<>();
            List<Double> times = new ArrayList<>();
            String[] line;
            for (int i = 1; i != n_cols; ++i) {
                line = variables.get(i - 1);
                System.out.printf("Output: %s.%s%n", line[0], line[1]);
                Namespace.Component comp = ns.components.get(line[0]);
                Type type = comp != null ? comp.outputs.get(line[1]) : null;
                if (type != null) {
                    names.add(line);
                    cols.add(i);
                    types.add(type);
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
                times.add(Double.parseDouble(line[0]));
                for (int i = 0; i != cols.size(); ++i) {
                    String v = line[cols.get(i)];
                    try {
                        vals.get(i).add(Double.parseDouble(v));
                    } catch (NumberFormatException e) {
                        throw new IOException("Line " + ln
                                + ": invalid value '" + v + "'");
                    }
                }
            }
            for (int i = 0; i != names.size(); ++i) {
                String[] n = names.get(i);
                double[] t = Doubles.toArray(times); 
                res.put(n[0], n[1],
                        ns.evaluator.makeTS(types.get(i), t, 
                                            Doubles.toArray(vals.get(i))));
            }
        }
    }
}
