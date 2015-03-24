package eu.cityopt.opt.ga;

import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerStateListener;

import com.google.inject.Inject;

import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.util.DelayedDeleter;

/**
 * Performs clean-up when optimisation stops.
 * This is run in the Opt4J GUI via CityoptFileModule.
 *
 * @author Hannu Rummukainen
 */
public class ModelBlobCleanup implements OptimizerStateListener {
    volatile SimulationModel model;
    volatile DelayedDeleter deleter;

    @Inject
    public ModelBlobCleanup(SimulationModel model) {
        this.model = model;
        this.deleter = DelayedDeleter.activate();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    void shutdown() {
        try {
            if (model != null) {
                model.close();
                model = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleter.tryDelete();
    }

    @Override
    public void optimizationStarted(Optimizer optimizer) {}

    @Override
    public void optimizationStopped(Optimizer optimizer) {
        shutdown();
    }
}
