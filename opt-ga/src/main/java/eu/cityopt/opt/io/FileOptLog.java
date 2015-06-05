package eu.cityopt.opt.io;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.script.ScriptException;

import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerStateListener;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

import eu.cityopt.sim.eval.SimulationFailure;
import eu.cityopt.sim.opt.OptimisationLog;

public class FileOptLog
implements OptimisationLog, OptimizerStateListener, Closeable {
    private final PrintStream ostr;
    private boolean isOpen;
    private boolean verbose = false;
    
    @Inject
    public FileOptLog(
            @Constant(namespace=FileOptLog.class, value = "filename")
            String filename)
                    throws FileNotFoundException {
        this.ostr = new PrintStream(filename);
        isOpen = true;
    }

    @Override
    public synchronized void close() throws IOException {
        if (isOpen) {
            isOpen = false;
            ostr.close();
        }
    }

    @Override
    public synchronized void logMessage(String text) {
        if (isOpen) {
            ostr.println(text);
        }
    }

    @Override
    public synchronized void logEvaluationFailure(
            String[] scenarioNameAndDescription,
            ScriptException exception) {
        OptimisationLog.super.logEvaluationFailure(
                scenarioNameAndDescription, exception);
        if (verbose && exception != null) {
            exception.printStackTrace(ostr);
        }
    }

    @Override
    public synchronized void logSimulationFailure(
            String[] scenarioNameAndDescription, SimulationFailure failure) {
        // TODO Auto-generated method stub
        OptimisationLog.super.logSimulationFailure(
                scenarioNameAndDescription, failure);
        if (verbose) {
            ostr.println(failure.getMessages());
        }
    }

    @Override
    public void optimizationStarted(Optimizer arg0) {
        logMessage("Optimisation started.");
    }

    @Override
    public void optimizationStopped(Optimizer arg0) {
        logMessage("Optimisation stopped.");
        try {
            close();
        } catch (IOException e) {
            System.err.println("Error closing log file.");
            e.printStackTrace();
        }
    }

    /**
     * Enable or disable verbose logging.
     * Whether to log exception backtraces and failed job logs.
     * @param verbose
     */
    @Inject(optional=true)
    public synchronized void setVerbose(
            @Constant(namespace=FileOptLog.class, value="verbose")
            boolean verbose) {
        this.verbose = verbose;
        logMessage("Verbose error logging "
                   + (verbose ? "enabled." : "disabled."));
    }
}
