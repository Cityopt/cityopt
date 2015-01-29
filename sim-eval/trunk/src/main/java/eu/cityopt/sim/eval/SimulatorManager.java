package eu.cityopt.sim.eval;

import java.io.IOException;

/**
 * Interface for simulator-specific code.
 * 
 * @see SimulatorManagers
 * @author Hannu Rummukainen
 */
public interface SimulatorManager {
    SimulationModel parseModel(byte[] modelData)
            throws IOException, SimulatorConfigurationException;

    SimulationRunner makeRunner(SimulationModel model, Namespace namespace)
            throws IOException, SimulatorConfigurationException;
}
