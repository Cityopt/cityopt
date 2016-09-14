package eu.cityopt.opt.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerStateListener;
import org.slf4j.LoggerFactory;

import eu.cityopt.opt.ga.adapter.SolutionTransformer;

/** Abstract base class for loggers using SolutionWriter.
 * 
 * @author ttekth
 */
public abstract class SolutionLogger
implements OptimizerStateListener, Closeable,
        org.opt4j.core.common.logger.Logger {
    /// For diagnostics when exceptions can't be thrown.
    protected final org.slf4j.Logger
            logger = LoggerFactory.getLogger(getClass());
    protected OutputStream out = null;
    protected final SolutionTransformer solxform;
    protected final SolutionWriter writer;

    public SolutionLogger(
            SolutionTransformer solxform, SolutionWriterFactory wfac,
            Path outfile) throws IOException {
        this.solxform = solxform;
        out = Files.newOutputStream(outfile);
        try {
            writer = wfac.create(out);
        } catch (RuntimeException e) {
            out.close();
            throw e;
        }
    }

    @Override
    public void optimizationStarted(Optimizer optimizer) {}

    /**
     * This just calls close.
     * Override if you want to write something at end of optimisation.
     * Just make sure to close when done.
     */
    @Override
    public void optimizationStopped(Optimizer optimizer) {
        try {
            close();
        } catch (IOException e) {
            logger.warn("Error closing output file", e);
        }
    }

    /**
     * Close the output file.
     * The file is automatically closed after writing the results, so normally
     * there is no need to call this.
     */
    @Override
    public synchronized void close() throws IOException {
        if (out != null) {
            try {
                writer.close();
                out.close();
            } finally {
                out = null;
            }
        }
    }
}
