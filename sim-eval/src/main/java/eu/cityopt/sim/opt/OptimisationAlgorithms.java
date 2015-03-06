package eu.cityopt.sim.opt;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import eu.cityopt.sim.eval.ConfigurationException;

/**
 * Access to SearchAlgorithm implementations by name.
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
     * Returns a SearchAlgorithm implementation by name.
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

    /** Registers a new SearchAlgorithm instance. */
    public static void register(String algorithmName, OptimisationAlgorithm searchAlgorithm) {
        algorithms.put(algorithmName, searchAlgorithm);
    }

    static {
        register("grid search", new GridSearchAlgorithm());
    }
}
