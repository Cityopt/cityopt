package eu.cityopt.sim.eval;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SimulationStorage implementation using a local hash table.
 *
 * @author Hannu Rummukainen
 */
public class HashSimulationStorage implements SimulationStorage {
    private Map<SimulationInput, Put> inputPutCache
            = new ConcurrentHashMap<>();

    @Override
    public SimulationOutput get(SimulationInput input) {
        Put put = inputPutCache.get(input);
        return put == null ? null : put.output;
    }

    @Override
    public void put(Put put) {
        if (put.output != null) {
            inputPutCache.put(put.input, put);
        }
    }

    @Override
    public void updateMetricValues(MetricValues metricValues) {
        // Not supported
    }

    @Override
    public Iterator<SimulationOutput> iterator() {
        return inputPutCache.values().stream().map(p -> p.output).iterator();
    }

    @Override
    public void close() throws IOException {
        inputPutCache.clear();
    }

    public MetricValues getMetrics(SimulationInput input) {
        Put put = inputPutCache.get(input);
        return put == null ? null : put.metricValues;
    }
}
