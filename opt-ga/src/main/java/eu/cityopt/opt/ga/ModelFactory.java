package eu.cityopt.opt.ga;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorManagers;

/**
 * A wrapper around {@link SimulatorManagers}.
 * The problem is static state in SimulationManagers: Simulators
 * must be registered using static methods and shut down by calling
 * another static method.  This class wraps those calls in dynamic objects:
 * simulators are registered by creating instances (of subclasses by simulator
 * type).  There is an instance counter: closing the last instance calls the
 * static shutdown method.
 * <p>
 * Things should work if you observe these practices:
 * <ul>
 * <li>Create all SimulationModels with ModelFactories.
 * <li>Close all SimulationModels and ModelFactories when done with them.
 * <li>Before closing a ModelFactory, close all SimulationModels created
 * by it.
 * <li>Do not create or close SimulationManager objects directly.
 * They are shared.
 * </ul>
 * 
 * @author ttekth
 */
public abstract class ModelFactory implements Closeable {
    private static int n = 0;
    private boolean closed = false;

    private static synchronized void incN() {
        ++n;
    }

    private static synchronized int decN() {
        return n == 0 ? 0 : --n;
    }

    protected ModelFactory() {
        incN();
    }

    /**
     * Load a simulation model from a stream.
     * @param simulator simulator name or null to discover from in.
     * @param in input stream to read the model from
     */
    public SimulationModel loadModel(String simulator, InputStream in)
            throws IOException, ConfigurationException {
        SimulationModel m = SimulatorManagers.parseModel(
                simulator, ByteStreams.toByteArray(in));
        if (m == null) {
            throw new ConfigurationException("Simulator detection failed");
        }
        return m;
    }

    @Override
    public synchronized void close() throws IOException {
        if (!closed) {
            closed = true;
            if (decN() == 0) {
                SimulatorManagers.shutdown();
            }
        }
    }

}