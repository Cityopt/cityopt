package eu.cityopt.sim.eval;

public interface SimulationStorage {

    public SimulationOutput get(SimulationInput input);

    public void put(SimulationOutput output);

    /** Updates the metric values of a previously stored successful simulation. */
    public void updateMetricValues(MetricValues metricValues);
}
