package eu.cityopt.sim.eval;

import java.util.HashMap;
import java.util.Map;

/**
 * Access to SimulatorManager implementations for named simulators.
 *
 * @author Hannu Rummukainen
 */
public class SimulatorManagers {
    private static Map<String, SimulatorManager> simulatorManagers
        = new HashMap<String, SimulatorManager>();

    public static void register(String profile, SimulatorManager manager) {
        simulatorManagers.put(profile, manager);
    }

    public static SimulatorManager get(String simulatorName) {
        return simulatorManagers.get(simulatorName);
    }
}
