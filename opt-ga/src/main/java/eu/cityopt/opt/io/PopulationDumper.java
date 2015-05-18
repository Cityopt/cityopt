package eu.cityopt.opt.io;

import java.io.IOException;
import java.nio.file.Paths;

import org.opt4j.core.Individual;
import org.opt4j.core.IndividualSet;
import org.opt4j.core.optimizer.Optimizer;
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
 */
public class PopulationDumper extends SolutionLogger  {
    private final IndividualSet population;

    @Inject
    public PopulationDumper(
            @Named("outputSet") IndividualSet population,
            SolutionTransformer solxform, SolutionWriterFactory wfac,
            @Constant(value="filename", namespace=PopulationDumper.class)
            String filename) throws IOException {
        super(solxform, wfac, Paths.get(filename));
        this.population = population;
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
                            ph.decisions,
                            solxform.makeSolutionFromIndividual(ind));
                }
                close();
            } catch (IOException e) {
                logger.error("Error writing population", e);
            }
        }
    }
}
