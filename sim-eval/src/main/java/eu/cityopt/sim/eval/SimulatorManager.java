package eu.cityopt.sim.eval;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for simulator-specific code.
 * 
 * @see SimulatorManagers
 * @author Hannu Rummukainen
 */
public interface SimulatorManager extends Closeable {
    String getSimulatorName();

    SimulationModel parseModel(InputStream modelData)
            throws IOException, ConfigurationException;

    SimulationRunner makeRunner(SimulationModel model, Namespace namespace)
            throws IOException, ConfigurationException;
}
