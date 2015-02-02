package eu.cityopt.sim.eval;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Access to SimulatorManager implementations for named simulators.
 *
 * @author Hannu Rummukainen
 */
public class SimulatorManagers {
    private static Map<String, SimulatorManager> simulatorManagers
        = new HashMap<String, SimulatorManager>();

    /** Returns the known simulator names. */
    public static Set<String> getSimulatorNames() {
        return Collections.unmodifiableSet(simulatorManagers.keySet());
    }

    /** Returns a SimulatorManager instance for access to a named simulator. */
    public static SimulatorManager get(String simulatorName) {
        return simulatorManagers.get(simulatorName);
    }

    /** Registers a new SimulatorManager instance. */
    public static void register(String profile, SimulatorManager manager) {
        simulatorManagers.put(profile, manager);
    }
}
