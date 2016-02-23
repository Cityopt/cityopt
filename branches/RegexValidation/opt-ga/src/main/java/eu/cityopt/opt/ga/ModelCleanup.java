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
public class ModelCleanup implements OptimizerStateListener {
    private SimulationModel model;
    private ModelFactory factory;
    private DelayedDeleter deleter;

    @Inject
    public ModelCleanup(SimulationModel model, ModelFactory factory) {
        this.model = model;
        this.factory = factory;
        this.deleter = DelayedDeleter.activate();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private synchronized void shutdown() {
        try {
            if (model != null) {
                model.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            model = null;
        }
        try {
            if (factory != null) {
                factory.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            factory = null;
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
