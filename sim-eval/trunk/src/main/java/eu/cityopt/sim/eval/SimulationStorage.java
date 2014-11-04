package eu.cityopt.sim.eval;

/**
 * Access to stored simulation runs for the sim-eval module and scenario
 * generation code. Database access should be provided via this interface.
 * 
 * @author Hannu Rummukainen <Hannu.Rummukainen@vtt.fi>
 */
public interface SimulationStorage {

    /**
     * Finds the simulation output that results with the given input, if
     * available.
     * 
     * @param input
     *            the simulation input data. Although SimulationInput refers to
     *            ExternalParameters, that reference is ignored here.
     * @return either the corresponding deterministic simulation output, or null
     *         if the output is not available.  The output may be either a
     *         SimulationFailure instance, or a SimulationResults instance.
     */
    public SimulationOutput get(SimulationInput input);

    /**
     * Stores the simulation output that results with the given input. The
     * output may be either a SimulationFailure instance or a SimulationResults
     * instance.
     * 
     * @param output
     *            the simulation output data, containing a reference to the
     *            corresponding input data. Although SimulationInput refers to
     *            ExternalParameters, that reference is ignored here.
     */
    public void put(SimulationOutput output);

    /** Updates the metric values of a previously stored successful simulation. */
    public void updateMetricValues(MetricValues metricValues);
}
