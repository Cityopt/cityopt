package eu.cityopt.opt.ga;

import java.text.ParseException;
import java.time.Instant;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.script.ScriptException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import eu.cityopt.opt.io.JacksonBinder;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.opt.OptimisationProblem;

/**
 * Initialise an {@link OptimisationProblem} from a {@link JacksonBinder}.
 * @author ttekth
 */
@Singleton
public class ProblemFromBinder implements Provider<OptimisationProblem> {
    private OptimisationProblem p;

    /**
     * Create an optimisation problem.
     * @param model the {@link SimulationModel}
     * @param t0 the time origin or null to retrieve from model.
     * @param binder the binder to create the problem data from.
     */
    @Inject
    public ProblemFromBinder(SimulationModel model,
                             @Named("timeOrigin") @Nullable Instant t0,
                             JacksonBinder binder)
                                     throws ParseException, ScriptException {
        Namespace ns = binder.makeNamespace(
                t0 != null ? t0 : model.getTimeOrigin());
        p = new OptimisationProblem(model, new ExternalParameters(ns));
        binder.addToProblem(p);
    }
    
    @Override
    public OptimisationProblem get() {return p;}
}
