package eu.cityopt.sim.eval;

import java.io.ByteArrayInputStream;
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
    /**
     * Parses raw model data into a usable object.
     * @param simulatorName either the specific simulator variant to use,
     *   or null to attempt auto-detection
     * @param modelData raw model data stream
     * @return model object
     * @throws AlienModelException if parsing fails but
     *   it is possible that the model is for a different simulator.
     */
    SimulationModel parseModel(String simulatorName, InputStream modelData)
            throws IOException, ConfigurationException;

    /**
     * Creates the context for running a model.
     * @param model the model to be run
     * @param namespace specifies the components, inputs and outputs
     * @return interface for running the model with different input values
     */
    SimulationRunner makeRunner(SimulationModel model, Namespace namespace)
            throws IOException, ConfigurationException;

    /**
     * Parses raw model data into a usable object.
     * @param simulatorName either the specific simulator variant to use,
     *   or null to attempt auto-detection
     * @param modelData raw model data bytes
     * @return model object
     * @throws AlienModelException if parsing fails but
     *   it is possible that the model is for a different simulator.
     */
    default SimulationModel parseModel(String simulatorName, byte[] modelData)
            throws IOException, ConfigurationException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(modelData)) {
            return parseModel(simulatorName, in);
        }
    }
}
