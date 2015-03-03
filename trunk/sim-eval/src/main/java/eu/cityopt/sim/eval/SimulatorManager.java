package eu.cityopt.sim.eval;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for simulator-specific code.
 * 
 * @see SimulatorManagers
 * @author Hannu Rummukainen
 */
public interface SimulatorManager {
    String getSimulatorName();

    SimulationModel parseModel(InputStream modelData)
            throws IOException, SimulatorConfigurationException;

    SimulationRunner makeRunner(SimulationModel model, Namespace namespace)
            throws IOException, SimulatorConfigurationException;
}
