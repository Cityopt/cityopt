package eu.cityopt.opt.io;

import java.io.IOException;
import java.nio.file.Paths;

import org.opt4j.core.Individual;
import org.opt4j.core.Individual.State;
import org.opt4j.core.IndividualStateListener;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

import eu.cityopt.opt.ga.CityoptPhenotype;
import eu.cityopt.opt.ga.adapter.SolutionTransformer;

/** Write Individuals into a file as they are evaluated.
 * 
 * @author ttekth
 */
public class EvaluationLogger extends SolutionLogger
implements IndividualStateListener {
    @Inject
    public EvaluationLogger(
            SolutionTransformer solxform, SolutionWriterFactory wfac,
            @Constant(value="filename", namespace=EvaluationLogger.class)
            String filename) throws IOException {
        super(solxform, wfac, Paths.get(filename));
    }
    
    @Override
    public void inidividualStateChanged(Individual ind) {
        if (ind.getState() == State.EVALUATED) {
            CityoptPhenotype ph = (CityoptPhenotype)ind.getPhenotype();
            try {
                writer.writeSolution(
                        ph.decisions,
                        solxform.makeSolutionFromIndividual(ind));
                writer.flush();
            } catch (IOException e) {
                logger.warn("Error writing evaluation result", e);
            }
        }
    }
}
