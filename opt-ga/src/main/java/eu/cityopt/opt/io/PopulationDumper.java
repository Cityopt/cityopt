package eu.cityopt.opt.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.opt4j.core.Individual;
import org.opt4j.core.IndividualSet;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerStateListener;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import eu.cityopt.opt.ga.CityoptPhenotype;
import eu.cityopt.opt.ga.adapter.SolutionTransformer;

/**
 * Write individuals into a file when optimisation stops.
 * Can be used on the archive or the whole population.
 * The file is opened immediately to detect problems early: no one wants to
 * wait hours for optimisation results only to find that there is no way
 * to save them.  Normally the file is closed after writing.  However,
 * if things go abnormally and writing never happens, you may need to close
 * the file explicitly with {@link #close()}.
 * @author ttekth
 *
 */
public class PopulationDumper implements OptimizerStateListener, Closeable {
    private final IndividualSet population;
    private OutputStream out = null;
    private final SolutionTransformer solxform;
    private final SolutionWriter writer;
    
    @Inject
    public PopulationDumper(
            @Named("outputSet") IndividualSet population,
            SolutionTransformer solxform, SolutionWriter writer,
            @Constant(value="filename", namespace=PopulationDumper.class)
            String filename) throws IOException {
        this.population = population;
        this.solxform = solxform;
        this.writer = writer;
        out = Files.newOutputStream(Paths.get(filename));
        writer.writeHeader(out);
    }

    @Override
    public void optimizationStarted(Optimizer optimizer) {}

    @Override
    public synchronized void optimizationStopped(Optimizer optimizer) {
        if (out != null) {
            try {
                for (Individual ind : population) {
                    CityoptPhenotype ph = (CityoptPhenotype)ind.getPhenotype();
                    writer.writeSolution(
                            out, ph.decisions,
                            solxform.makeSolutionFromIndividual(ind));
                }
                close();
            } catch (IOException e) {
                // Not much we can do here.
                e.printStackTrace();
            }
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
                out.close();
            } finally {
                out = null;
            }
        }
    }
}
