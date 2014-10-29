package eu.cityopt.sim.eval;

import java.util.HashMap;
import java.util.Map;

/**
 * SimulationStorage implementation using a local hash table.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public class HashSimulationStorage implements SimulationStorage {
    private Map<SimulationInput, SimulationOutput> inputOutputCache;

    public HashSimulationStorage() {
        this.inputOutputCache = new HashMap<SimulationInput, SimulationOutput>();
    }

    @Override
    public SimulationOutput get(SimulationInput input) {
        return inputOutputCache.get(input);
    }

    @Override
    public void put(SimulationOutput output) {
        inputOutputCache.put(output.getInput(), output);
    }

    @Override
    public void updateMetricValues(MetricValues metricValues) {
        // Not supported
    }
}
