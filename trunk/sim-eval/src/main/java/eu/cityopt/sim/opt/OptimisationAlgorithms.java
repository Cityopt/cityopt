package eu.cityopt.sim.opt;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import eu.cityopt.sim.eval.ConfigurationException;

/**
 * Access to {@link OptimisationAlgorithm} implementations by name.
 *
 * @author Hannu Rummukainen
 */
public class OptimisationAlgorithms {
    private static Map<String, OptimisationAlgorithm>
        algorithms = new ConcurrentHashMap<>();

    /** Returns the known algorithm names. */
    public static Set<String> getAlgorithmNames() {
        return Collections.unmodifiableSet(algorithms.keySet());
    }

    /**
     * Returns an {@link OptimisationAlgorithm} implementation by name.
     * @throws ConfigurationException if the name is unknown 
     */
    public static OptimisationAlgorithm get(String algorithmName)
            throws ConfigurationException {
        OptimisationAlgorithm algorithm = algorithms.get(algorithmName);
        if (algorithm == null) {
            throw new ConfigurationException("Unknown algorithm " + algorithmName);
        }
        return algorithm;
    }

    /** Registers a new {@link OptimisationAlgorithm} instance. */
    public static void register(OptimisationAlgorithm algorithm) {
        algorithms.put(algorithm.getName(), algorithm);
    }
}
