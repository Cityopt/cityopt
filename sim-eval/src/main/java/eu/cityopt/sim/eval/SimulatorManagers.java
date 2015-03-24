package eu.cityopt.sim.eval;

import java.io.IOException;
import java.util.Collections;
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
