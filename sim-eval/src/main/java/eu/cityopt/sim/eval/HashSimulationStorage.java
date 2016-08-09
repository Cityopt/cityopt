package eu.cityopt.sim.eval;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SimulationStorage implementation using a local hash table.
 * 
 * @author Hannu Rummukainen
 */
public class HashSimulationStorage implements SimulationStorage {
    private ConcurrentMap<SimulationInput, SimulationOutput> inputOutputCache;

    public HashSimulationStorage() {
        this.inputOutputCache = new ConcurrentHashMap<SimulationInput, SimulationOutput>();
    }

    @Override
    public SimulationOutput get(SimulationInput input) {
        return inputOutputCache.get(input);
    }

    @Override
    public void put(Put put) {
        if (put.output != null) {
            inputOutputCache.put(put.input, put.output);
        }
    }

    @Override
    public void updateMetricValues(MetricValues metricValues) {
        // Not supported
    }

    @Override
    public Iterator<SimulationOutput> iterator() {
        return inputOutputCache.values().iterator();
    }

    @Override
    public void close() throws IOException {
        inputOutputCache.clear();
    }
}
