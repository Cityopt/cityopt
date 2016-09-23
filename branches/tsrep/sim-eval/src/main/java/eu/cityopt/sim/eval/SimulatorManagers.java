package eu.cityopt.sim.eval;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access to SimulatorManager implementations for named simulators.
 *
 * @author Hannu Rummukainen
 */
public class SimulatorManagers {
    private static Logger logger = LoggerFactory.getLogger(SimulatorManagers.class);
    private static Map<String, SimulatorManager> simulatorManagers
        = new ConcurrentHashMap<String, SimulatorManager>();

    /** Returns the known simulator names. */
    public static Set<String> getSimulatorNames() {
        return Collections.unmodifiableSet(simulatorManagers.keySet());
    }

    /**
     * Returns a SimulatorManager instance for access to a named simulator.
     * @throws ConfigurationException if the name is unknown
     */
    public static SimulatorManager get(String simulatorName)
            throws ConfigurationException {
        SimulatorManager manager = simulatorManagers.get(simulatorName);
        if (manager == null) {
            throw new ConfigurationException(
                    "Unknown simulator " + simulatorName);
        }
        return manager;
    }

    /**
     * Attempts to find a SimulatorManager instance that can run the given model.
     * Returns a parsed SimulationModel on success, and null if the detection fails.
     */
    public static SimulationModel detectSimulator(byte[] modelData)
            throws IOException, ConfigurationException {
        // First find distinct SimulatorManager instances.
        Set<SimulatorManager> distinctManagers = new HashSet<>();
        for (SimulatorManager manager : simulatorManagers.values()) {
            distinctManagers.add(manager);
        }
        // Then try to parse the model with each of them.
        for (SimulatorManager manager: distinctManagers) {
            try {
                return manager.parseModel(null, modelData);
            } catch (AlienModelException e) {
                logger.debug("While detecting simulator: No success with "
                             + manager, e);
            }
        }
        return null;
    }

    /**
     * Parse a model of any supported simulator.
     *
     * @param simulatorName the name of the simulator variant to be used,
     *    or null if not known.  If null, then parsing will be attempted
     *    using support code for all known simulators, and if all of them fail,
     *    a ConfigurationException is thrown.
     * @param modelData the raw model data
     * @return handle to model structure
     * @throws ConfigurationException if there is an error in the model data, or
     *    a suitable simulator cannot be determined
     * @throws IOException if the parsing fails
     */
    public static SimulationModel parseModel(String simulatorName, byte[] modelData)
            throws IOException, ConfigurationException {
        SimulationModel model = null;
        if (simulatorName == null) {
            model = detectSimulator(modelData);
            if (model == null) {
                throw new ConfigurationException(
                        "Failed to detect simulator - please select a simulator explicitly");
            }
            return model;
        } else {
            SimulatorManager manager = get(simulatorName);
            return manager.parseModel(simulatorName, modelData);
        }
    }

    /** Registers a new SimulatorManager instance. */
    public static void register(String simulatorName, SimulatorManager manager) {
        SimulatorManager oldManager = (manager != null)
                ? simulatorManagers.put(simulatorName, manager)
                : simulatorManagers.remove(simulatorName);
        if (oldManager != null) {
            tryClose(oldManager);
        }
    }

    /**
     * Shuts down and de-registers all SimulatorManager instances.
     * Exceptions are ignored.
     */
    public static void shutdown() {
        Iterator<Map.Entry<String, SimulatorManager>> it =
                simulatorManagers.entrySet().iterator();
        while (it.hasNext()) {
            tryClose(it.next().getValue());
            it.remove();
        }
    }

    private static void tryClose(SimulatorManager manager) {
        try {
            manager.close();
        } catch (IOException e) {
            logger.warn("Failed to close SimulatorManager: " + e.getMessage());
        }
    }
}
